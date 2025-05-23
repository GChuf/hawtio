package io.hawt.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Spring AutoConfiguration for the management endpoint /hawtio/plugin
 */
@ManagementContextConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class HawtioPluginAutoConfiguration {

    /**
     * <p>This bean creates a Spring controller that is added to the ManagementContext (/actuator).</p>
     *
     * <p>We can't simply have a {@link Bean @Bean} method that returns {@link Controller @Controller} annotated class
     * because we need dynamic mapping, which can't be specified using
     * {@link org.springframework.web.bind.annotation.RequestMapping}. And that's what
     * {@link RequestMappingHandlerMapping} is doing internally. We have to register the handler method
     * programmatically.</p>
     *
     * @param endpointPathResolver
     * @param requestMappingHandlerMapping
     * @param plugins
     * @return
     * @throws NoSuchMethodException
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(EndpointPathResolver.class)
    public HawtioPluginController hawtioRequestHandler(final EndpointPathResolver endpointPathResolver,
                                                       // This Qualifier is mandatory right now, but once the deprecated classes will be removed in SB 3.5, the Qualifier can be removed as well
                                                       @Autowired @Qualifier("requestMappingHandlerMapping") final RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                       @Autowired final Optional<List<HawtioPlugin>> plugins) throws NoSuchMethodException {
        HawtioPluginController hawtioRequestHandler = new HawtioPluginController(plugins.orElse(Collections.emptyList()));

        Method getPlugins = HawtioPluginController.class.getMethod("getPlugins");
        RequestMappingInfo getPluginsMappingInfo = RequestMappingInfo
            .paths(endpointPathResolver.resolveUrlMapping("hawtio") + "/plugin")
            .methods(RequestMethod.GET)
            .build();
        requestMappingHandlerMapping.registerMapping(getPluginsMappingInfo, hawtioRequestHandler, getPlugins);

        return hawtioRequestHandler;
    }

}
