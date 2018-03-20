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

package com.github.mishaninss.uidriver.webdriver.capabilities;

import com.github.mishaninss.data.WebDriverProperties;
import com.github.mishaninss.uidriver.webdriver.webdrivercreators.MultiBrowserCreator;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component(MultiBrowserCreator.FIREFOX_CAPABILITIES_PROVIDER)
public class DefaultFirefoxCapabilitiesProviderImpl implements ICapabilitiesProvider {

    @Autowired
    private WebDriverProperties properties;

    private DesiredCapabilities getFirefoxCapabilities(){
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.startup.homepage", properties.application().url);
        profile.setPreference("plugin.state.npdeployjava", 0);
        profile.setAcceptUntrustedCertificates(true);
        profile.setPreference("security.enable_java", true);

        DesiredCapabilities caps = DesiredCapabilities.firefox();
        caps.setCapability(FirefoxDriver.PROFILE, profile);

        LoggingPreferences loggingprefs = new LoggingPreferences();
        loggingprefs.enable(LogType.BROWSER, Level.ALL);
        caps.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
        caps.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,  UnexpectedAlertBehaviour.ACCEPT);

        return caps;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        return getFirefoxCapabilities();
    }
}
