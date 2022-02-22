/*
 * Copyright (c) 2021 Sergey Mishanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mishaninss.arma.uidriver.webdriver.firefox;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.arma.uidriver.webdriver.ICapabilitiesProvider;
import com.github.mishaninss.arma.uidriver.webdriver.IWebDriverCreator;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("firefox")
public class FirefoxBrowserCreator implements IWebDriverCreator {

  private static final String COULD_NOT_START_SESSION_MESSAGE = "Could not start a new browser session";

  @Autowired
  private WebDriverProperties properties;
  @Autowired
  private ICapabilitiesProvider capabilitiesProvider;

  @Override
  public WebDriver createDriver(Capabilities desiredCapabilities) {
    WebDriver webDriver;

    Capabilities capabilities =
        desiredCapabilities != null ? desiredCapabilities : capabilitiesProvider.getCapabilities();
    try {
      if (properties.driver().isRemote()) {
        String gridUrl = properties.driver().gridUrl;
        webDriver = new RemoteWebDriver(new URL(gridUrl), capabilities);
      } else {
        WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
        webDriver = new FirefoxDriver(new FirefoxOptions(capabilities));
      }
    } catch (Exception ex) {
      throw new FrameworkConfigurationException(COULD_NOT_START_SESSION_MESSAGE, ex);
    }

    webDriver.manage().timeouts()
        .implicitlyWait(properties.driver().timeoutsElement, TimeUnit.MILLISECONDS);
    webDriver.manage().timeouts()
        .pageLoadTimeout(properties.driver().timeoutsPageLoad, TimeUnit.MILLISECONDS);
    return webDriver;
  }
}
