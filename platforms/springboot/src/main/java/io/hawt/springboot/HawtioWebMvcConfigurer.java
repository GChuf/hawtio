package io.hawt.springboot;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>{@link WebMvcConfigurer} that adds static resource handlers for Hawtio deployment on Spring Boot.</p>
 *
 * <p>Note: We can't simply have {@link org.springframework.stereotype.Controller @Controller} annotated
 * bean with {@link org.springframework.web.bind.annotation.RequestMapping @RequestMapping}, because we need
 * dynamic URI paths.</p>
 */
public class HawtioWebMvcConfigurer implements WebMvcConfigurer {

    private final EndpointPathResolver endpointPath;

    public HawtioWebMvcConfigurer(final EndpointPathResolver endpointPath) {
        this.endpointPath = endpointPath;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // this doesn't work when request.getServletContext().getMimeType(resource.getFilename())
        // resolves to non-null type...
        configurer.mediaType("html", new MediaType("text", "html", StandardCharsets.UTF_8));
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // we don't have to add separate resources for each subdirectory - top level handler is enough
        // "classpath:" prefixed resources will be added as ClassPathContextResource
        // non URI resources (like "/") will be added as ServletContextResource (because we're in web app!)
        registry
            .addResourceHandler(endpointPath.resolveUrlMapping("hawtio", "/**"))
            .addResourceLocations("/", "classpath:/hawtio-static/");
    }

}
