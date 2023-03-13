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

package com.github.mishaninss.arma.uidriver.webdriver.firefox;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.uidriver.webdriver.ICapabilitiesProvider;
import java.util.logging.Level;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("firefox")
public class DefaultFirefoxCapabilitiesProviderImpl implements ICapabilitiesProvider {

  @Autowired
  private WebDriverProperties properties;

  private MutableCapabilities getFirefoxCapabilities() {
    FirefoxOptions profile = new FirefoxOptions();
    profile.setCapability("browser.startup.homepage", properties.application().url);
    profile.setCapability("plugin.state.npdeployjava", 0);
    profile.setAcceptInsecureCerts(true);
    profile.setCapability("security.enable_java", true);

    LoggingPreferences loggingprefs = new LoggingPreferences();
    loggingprefs.enable(LogType.BROWSER, Level.ALL);

    return profile;
  }

  private MutableCapabilities getChromeCapabilities() {
    return getFirefoxCapabilities();
  }

  @Override
  public MutableCapabilities getCapabilities() {
    return getFirefoxCapabilities();
  }
}
