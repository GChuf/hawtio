package io.hawt.tests.features.setup;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;
import java.util.Arrays;

import io.hawt.tests.features.config.TestConfiguration;

public class WebDriver {
    private static final Logger LOG = LoggerFactory.getLogger(WebDriver.class);

    /**
     * Set up a web driver.
     */
    public static void setup() {
        LOG.info("Setting up a web browser options");
        if (TestConfiguration.isRunningInContainer()) {
            setupDriverPaths();
            System.setProperty(GeckoDriverService.GECKO_DRIVER_LOG_PROPERTY, "target/driver.log");
            System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "target/driver.log");
        }
        System.setProperty("hawtio.proxyWhitelist", "localhost, 127.0.0.1");
        if (Configuration.browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", ImmutableMap.of("credentials_enable_service", false, "profile.password_manager_enabled", false, "profile.password_manager_leak_detection", false));
            options.addArguments("--proxy-bypass-list=\"<-loopback>\"");
            Configuration.browserCapabilities = options;
        } else {
            FirefoxOptions options = new FirefoxOptions();
            options.addPreference("network.proxy.allow_hijacking_localhost", false);
            //Trust the docker internal network
            options.addPreference("dom.securecontext.allowlist", "172.17.0.1");
            Configuration.browserCapabilities = options;
        }
        Configuration.headless = TestConfiguration.browserHeadless();
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 20000;

    }

    /**
     * Setup drivers when running in container - avoid fetching driver from Internet
     * every run
     */
    private static void setupDriverPaths() {
        Path optFolder = Path.of("/", "opt");
        Arrays.stream(optFolder.toFile().list()).filter(f -> f.contains("geckodriver")).findFirst().ifPresent(path -> {
            System.setProperty("webdriver.firefox.driver", optFolder.resolve(path).toAbsolutePath().toString());
        });
        Path seleniumFolder = optFolder.resolve("selenium");
        Arrays.stream(seleniumFolder.toFile().list()).filter(f -> f.startsWith("chromedriver")).findFirst()
                .ifPresent(path -> {
                    System.setProperty("webdriver.chrome.driver",
                            seleniumFolder.resolve(path).toAbsolutePath().toString());
                });
    }

    /**
     * Clear cache, cookies, local storage after the tests are run.
     */
    public static void clear() {
        LOG.info("Clear browser cookies and local storage");
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }
}
