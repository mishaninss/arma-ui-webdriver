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

import com.github.mishaninss.uidriver.webdriver.WebDriverFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Provides a single instance of WebDriver
 *
 * @author Sergey Mishanin
 */
@Component
@Profile("chrome")
public class ChromeDriverFactory extends WebDriverFactory {

    @Autowired
    private IChromeDriverServiceCreator chromeDriverServiceCreator;

    /**
     * nulls WebDriver instance
     */
    @Override
    public void hardCloseDriver() {
        super.hardCloseDriver();
        chromeDriverServiceCreator.terminateChrome();
    }

    @Override
    public String getSessionId() {
        if (!isBrowserStarted()) {
            return null;
        }
        SessionId sessionId = driver instanceof RemoteWebDriver ?
                ((RemoteWebDriver) driver).getSessionId() :
                ((ChromeDriver) driver).getSessionId();

        return sessionId.toString();
    }
}