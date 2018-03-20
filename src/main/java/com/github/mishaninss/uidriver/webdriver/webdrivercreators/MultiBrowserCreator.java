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

package com.github.mishaninss.uidriver.webdriver.webdrivercreators;

import com.github.mishaninss.data.WebDriverProperties;
import com.github.mishaninss.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.uidriver.webdriver.BrowserNames;
import com.github.mishaninss.uidriver.webdriver.IWebDriverCreator;
import com.github.mishaninss.uidriver.webdriver.NetworkConditions;
import com.github.mishaninss.uidriver.webdriver.capabilities.ICapabilitiesProvider;
import com.github.mishaninss.uidriver.webdriver.chrome.ExtendedChromeDriver;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Component
public class MultiBrowserCreator implements IWebDriverCreator {
    public static final String CHROME_CAPABILITIES_PROVIDER = "ChromeCapabilities";
    public static final String FIREFOX_CAPABILITIES_PROVIDER = "FirefoxCapabilities";
    private static final String COULD_NOT_START_SESSION_MESSAGE = "Could not start a new browser session";

    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver webDriver;

        String browserNameVal = properties.driver().browserName;
        if (StringUtils.isBlank(browserNameVal)){
            if (desiredCapabilities != null){
                browserNameVal = desiredCapabilities.getBrowserName();
            }
            if (StringUtils.isBlank(browserNameVal)) {
                throw new FrameworkConfigurationException(WebDriverProperties.Driver.BROWSER_NAME + " property was not set");
            }
        }

        BrowserNames browserName;
        try {
            browserName = BrowserNames.valueOf(browserNameVal.toUpperCase());
        } catch (IllegalArgumentException ex){
            throw new FrameworkConfigurationException("Unknown browser name: " + browserNameVal);
        }

        DesiredCapabilities capabilities;
        try {
            switch (browserName) {
                case CHROME:
                    capabilities = desiredCapabilities != null
                            ? desiredCapabilities
                            : applicationContext.getBean(CHROME_CAPABILITIES_PROVIDER, ICapabilitiesProvider.class).getCapabilities();
                    if (properties.driver().isRemote()) {
                        String gridUrl = properties.driver().gridUrl;
                        webDriver = new ExtendedChromeDriver(new URL(gridUrl), capabilities);
                    } else {
                        ChromeDriverManager.getInstance().setup();
                        webDriver = new ExtendedChromeDriver(capabilities);
                    }
                    NetworkConditions networkConditions = properties.driver().getNetworkConditions();
                    if (networkConditions != null) {
                        ((ExtendedChromeDriver) webDriver).setNetworkConditions(networkConditions);
                    }
                    break;
                case FIREFOX:
                    capabilities = desiredCapabilities != null
                            ? desiredCapabilities
                            : applicationContext.getBean(FIREFOX_CAPABILITIES_PROVIDER, ICapabilitiesProvider.class).getCapabilities();
                    if (properties.driver().isRemote()) {
                        String gridUrl = properties.driver().gridUrl;
                        webDriver = new RemoteWebDriver(new URL(gridUrl), capabilities);
                    } else {
                        ChromeDriverManager.getInstance().setup();
                        webDriver = new FirefoxDriver(capabilities);
                    }
                    break;
                default:
                    throw new FrameworkConfigurationException("Unsupported browser type: " + browserName);
            }
        } catch (Exception ex){
            throw new FrameworkConfigurationException(COULD_NOT_START_SESSION_MESSAGE ,ex);
        }

        webDriver.manage().timeouts().implicitlyWait(properties.driver().timeoutsElement, TimeUnit.MILLISECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(properties.driver().timeoutsPageLoad, TimeUnit.MILLISECONDS);
        return webDriver;
    }
}
