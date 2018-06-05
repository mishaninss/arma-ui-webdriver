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

package com.github.mishaninss.uidriver.webdriver.chrome;

import com.github.mishaninss.data.WebDriverProperties;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.webdriver.DesiredCapabilitiesLoader;
import com.github.mishaninss.uidriver.webdriver.ICapabilitiesProvider;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Component
@Profile("chrome")
public class DefaultChromeCapabilitiesProviderImpl implements ICapabilitiesProvider {
    public static final String CAPABILITIES_PROPERTY_PREFIX = "arma.driver.chrome.capability.";
    public static final String CAPABILITIES_FILE_PROPERTY = "arma.driver.chrome.capabilities.file";
    private static final String DEFAULT_CHROME_CAPABILITIES_FILE = "./chrome_capabilities.properties";
    @Value("${" + CAPABILITIES_FILE_PROPERTY + ":" + DEFAULT_CHROME_CAPABILITIES_FILE + "}")
    public String capabilitiesFile;

    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private DesiredCapabilitiesLoader capabilitiesLoader;
    @Reporter
    private IReporter reporter;

    private Map<String, Object> getChromeOptions(){
        Map<String, Object> chromeOptions = new HashMap<>();
        List<String> args = new ArrayList<>();
        args.add("disable-blink-features=BlockCredentialedSubresources");
        args.add("disable-infobars");
        args.add("start-maximized");
        chromeOptions.put("args", args);
        if (properties.driver().shouldCollectPerfLogs()) {
            Map<String, Object> perfLogPrefs = new HashMap<>();
            if (properties.driver().collectTracingLogs) {
                perfLogPrefs.put("traceCategories", "blink.user_timing, loading");
            }
            if (properties.driver().collectNetworkLogs) {
                perfLogPrefs.put("enableNetwork", true);
            }
            chromeOptions.put("perfLoggingPrefs", perfLogPrefs);
        }
        return chromeOptions;
    }

    private DesiredCapabilities getChromeCapabilities(){
        DesiredCapabilities caps = DesiredCapabilities.chrome();
        caps.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
        LoggingPreferences logPrefs = new LoggingPreferences();
        if (properties.driver().areConsoleLogsEnabled()){
            logPrefs.enable(LogType.BROWSER, properties.driver().browserLogsLevel);
        }
        if (properties.driver().shouldCollectPerfLogs()) {
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        }
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        setUnexpectedAlertBehaviour(caps);
        return caps;
    }

    private DesiredCapabilities getGridCapabilities(){
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
        if (properties.driver().shouldCollectPerfLogs()) {
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        }
        caps.setBrowserName(properties.driver().browserName);
        String browserVersion = properties.driver().browserName;
        if (StringUtils.isNoneBlank(browserVersion)) {
            caps.setVersion(browserVersion);
        }
        String platform = properties.driver().platformName;
        if (StringUtils.isNoneBlank(platform)) {
            caps.setPlatform(Platform.valueOf(platform.toUpperCase()));
        }
        setUnexpectedAlertBehaviour(caps);
        return caps;
    }

    private void setUnexpectedAlertBehaviour(DesiredCapabilities caps){
        String unexpectedAlertBehaviour = properties.driver().unexpectedAlertBehaviour;
        if (StringUtils.isNoneBlank(unexpectedAlertBehaviour)) {
            switch (unexpectedAlertBehaviour.toLowerCase()) {
                case "accept":
                    caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                    break;
                case "dismiss":
                    caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.DISMISS);
                    break;
                default:
                    caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
            }
        }
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities;
        if (properties.driver().isRemote()){
            capabilities = getGridCapabilities();
        } else {
            capabilities = getChromeCapabilities();
        }

        capabilities.merge(capabilitiesLoader.loadCapabilities(capabilitiesFile));
        capabilities.merge(capabilitiesLoader.loadEnvironmentProperties());
        capabilities.merge(capabilitiesLoader.loadEnvironmentProperties(CAPABILITIES_PROPERTY_PREFIX));
        reporter.debug("Loaded capabilities: " + capabilities);
        return capabilities;
    }
}
