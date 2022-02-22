package com.github.mishaninss.arma.data;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.uidriver.webdriver.NetworkConditions;

/**
 * Provides an Application Context properties
 *
 * @author Sergey Mishanin
 */
@Component
public class WebDriverProperties {

    @Autowired
    @Qualifier("webDriverDriverProps")
    private Driver driverProps;
    @Autowired
    @Qualifier("uiCommonsApplicationProps")
    private UiCommonsProperties.Application applicationProps;
    @Autowired
    @Qualifier("uiCommonsFrameworkProps")
    private UiCommonsProperties.Framework frameworkProps;

    public Driver driver() {
        return driverProps;
    }

    public UiCommonsProperties.Application application() {
        return applicationProps;
    }

    public UiCommonsProperties.Framework framework() {
        return frameworkProps;
    }

    /**
     * Contains a list of available environment property names
     */
    @Component("webDriverDriverProps")
    public static class Driver extends UiCommonsProperties.Driver {

        public static final String COLLECT_TRACING_LOGS = "arma.driver.collect.tracing.logs";
        public static final String GRID_URL = "arma.driver.grid.url";
        public static final String DEVICE_NAME = "arma.driver.device.name";
        public static final String PLATFORM_NAME = "arma.driver.platform.name";
        public static final String PLATFORM_VERSION = "arma.driver.platform.version";
        public static final String BROWSER_NAME = "arma.driver.browser.name";
        public static final String BROWSER_VERSION = "arma.driver.browser.version";
        public static final String SCREEN_RESOLUTION = "arma.driver.screen.resolution";
        public static final String NETWORK_CONDITIONS = "arma.driver.network.conditions";
        public static final String UNEXPECTED_ALERT_BEHAVIOUR = "arma.driver.unexpected.alert.behaviour";
        public static final String COLLECT_NETWORK_LOGS = "arma.driver.collect.network.logs";

        @Value("${" + COLLECT_NETWORK_LOGS + ":false}")
        public boolean collectNetworkLogs;

        @Value("${" + COLLECT_TRACING_LOGS + ":false}")
        public boolean collectTracingLogs;

        @Value("${" + GRID_URL + ":}")
        public String gridUrl;

        @Value("${" + DEVICE_NAME + ":}")
        public String deviceName;

        @Value("${" + PLATFORM_NAME + ":}")
        public String platformName;

        @Value("${" + PLATFORM_VERSION + ":}")
        public String platformVersion;

        @Value("${" + BROWSER_NAME + ":chrome}")
        public String browserName;

        @Value("${" + BROWSER_VERSION + ":}")
        public String browserVersion;

        @Value("${" + SCREEN_RESOLUTION + ":}")
        public String screenResolution;

        @Value("${" + NETWORK_CONDITIONS + ":}")
        public String networkConditions;

        @Value("${" + UNEXPECTED_ALERT_BEHAVIOUR + ":}")
        public String unexpectedAlertBehaviour;

        public boolean shouldCollectPerfLogs() {
            return collectTracingLogs || collectNetworkLogs;
        }

        public boolean isRemote() {
            return StringUtils.isNoneBlank(gridUrl);
        }

        public NetworkConditions getNetworkConditions() {
            if (StringUtils.isNoneBlank(networkConditions)) {
                try {
                    return NetworkConditions.valueOf(networkConditions.toUpperCase());
                } catch (Exception ex) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}