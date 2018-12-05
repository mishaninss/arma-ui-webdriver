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
import com.github.mishaninss.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.webdriver.ICapabilitiesProvider;
import com.github.mishaninss.uidriver.webdriver.IWebDriverCreator;
import com.github.mishaninss.uidriver.webdriver.NetworkConditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Component
@Profile("chrome")
public class ChromeBrowserCreator implements IWebDriverCreator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String COULD_NOT_START_SESSION_MESSAGE = "Could not start a new browser session";

    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private ICapabilitiesProvider capabilitiesProvider;
    @Autowired
    private IChromeDriverServiceCreator chromeDriverServiceCreator;
    @Reporter
    private IReporter reporter;

    @Override
    public WebDriver createDriver(Capabilities desiredCapabilities) {
        WebDriver webDriver;

        Capabilities capabilities = capabilitiesProvider.getCapabilities();
        capabilities.merge(desiredCapabilities);
        reporter.debug("Final desired capabilities: {}", GSON.toJson(capabilities));

        try {
            if (properties.driver().isRemote()) {
                String gridUrl = properties.driver().gridUrl;
                webDriver = new ExtendedChromeDriver(new URL(gridUrl), capabilities);
            } else {
                ChromeDriverManager.getInstance().setup();
                ChromeDriverService chromeDriverService = chromeDriverServiceCreator.getChromeDriverService();
                if (!chromeDriverService.isRunning()){
                    chromeDriverService.start();
                }
                webDriver = new ExtendedChromeDriver(chromeDriverService, capabilities);
            }
            NetworkConditions networkConditions = properties.driver().getNetworkConditions();
            if (networkConditions != null) {
                ((ExtendedChromeDriver) webDriver).setNetworkConditions(networkConditions);
            }
        } catch (Exception ex){
            throw new FrameworkConfigurationException(COULD_NOT_START_SESSION_MESSAGE ,ex);
        }

        webDriver.manage().timeouts().implicitlyWait(properties.driver().timeoutsElement, TimeUnit.MILLISECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(properties.driver().timeoutsPageLoad, TimeUnit.MILLISECONDS);
        return webDriver;
    }
}
