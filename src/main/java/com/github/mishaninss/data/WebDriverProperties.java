/*
 * Copyright 2018 Sergey Mishanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mishaninss.data;

import com.github.mishaninss.uidriver.webdriver.NetworkConditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides an Application Context properties
 * @author Sergey Mishanin
 */
@Component
public class WebDriverProperties {

    @Autowired
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
    @Component
    public static class Driver extends UiCommonsProperties.Driver{
        public static final String COLLECT_NETWORK_LOGS = "taf.driver.collect.network.logs";
        public static final String COLLECT_TRACING_LOGS = "taf.driver.collect.tracing.logs";
        public static final String GRID_URL = "taf.driver.grid.url";
        public static final String DEVICE_NAME = "taf.driver.device.name";
        public static final String PLATFORM_NAME = "taf.driver.platform.name";
        public static final String PLATFORM_VERSION = "taf.driver.platform.version";
        public static final String BROWSER_NAME = "taf.driver.browser.name";
        public static final String BROWSER_VERSION = "taf.driver.browser.version";
        public static final String SCREEN_RESOLUTION = "taf.driver.screen.resolution";
        public static final String NETWORK_CONDITIONS = "taf.driver.network.conditions";
        public static final String UNEXPECTED_ALERT_BEHAVIOUR = "taf.driver.unexpected.alert.behaviour";

        @Value("${" + COLLECT_NETWORK_LOGS + ":false}")
        public boolean collectNetworkLogs;
        @Value("${" + COLLECT_TRACING_LOGS + ":false}")
        public boolean collectTracingLogs;
        @Value("${" + GRID_URL + ":}")
        public String gridUrl;
        public String deviceName = "taf.driver.device.name";
        public String platformName = "taf.driver.platform.name";
        public String platformVersion = "taf.driver.platform.version";
        @Value("${" + BROWSER_NAME + ":chrome}")
        public String browserName;
        public String browserVersion = "taf.driver.browser.version";
        public String screenResolution = "taf.driver.screen.resolution";
        @Value("${" + NETWORK_CONDITIONS + ":}")
        public String networkConditions;
        @Value("${" + UNEXPECTED_ALERT_BEHAVIOUR + ":}")
        public String unexpectedAlertBehaviour;

        public boolean shouldCollectPerfLogs(){
            return collectTracingLogs || collectNetworkLogs;
        }

        public boolean isRemote(){
            return StringUtils.isNoneBlank(gridUrl);
        }

        public NetworkConditions getNetworkConditions(){
            if (StringUtils.isNoneBlank(networkConditions)) {
                try {
                    return NetworkConditions.valueOf(networkConditions.toUpperCase());
                } catch (Exception ex){
                    return null;
                }
            } else{
                return null;
            }
        }
    }
}