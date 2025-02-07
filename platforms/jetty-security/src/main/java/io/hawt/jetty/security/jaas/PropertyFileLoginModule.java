package io.hawt.jetty.security.jaas;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.eclipse.jetty.security.jaas.JAASLoginService;

/**
 * Hawtio specific {@link org.eclipse.jetty.security.jaas.spi.PropertyFileLoginModule} which doesn't require
 * {@code <login-config>} configuration in {@code web.xml}.
 */
public class PropertyFileLoginModule extends org.eclipse.jetty.security.jaas.spi.PropertyFileLoginModule {

    public static final String DEFAULT_FILENAME = "realm.properties";

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        JAASLoginService.INSTANCE.set(new HawtioJAASLoginService());
        Object file = options.get("file");
        if (file == null) {
            file = System.getProperty("login.file", DEFAULT_FILENAME);
            Map<String, Object> newOptions = new HashMap<>(options);
            newOptions.put("file", file);
            options = newOptions;
        }
        try {
            super.initialize(subject, callbackHandler, sharedState, options);
        } finally {
            JAASLoginService.INSTANCE.remove();
        }
    }

    @Override
    public boolean logout() {
        // Jetty assumes that LoginContext instance is stored inside
        // org.eclipse.jetty.security.jaas.JAASUserPrincipal._loginContext
        // and that javax.security.auth.login.LoginContext.init was called once.
        // Normally multiple LoginContext instances (one for login, one for logout) operate on single Subject.
        return true;
    }

}
