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

package com.github.mishaninss.uidriver.webdriver;

import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link IAlertHandler} interface based on WebDriver engine
 * Provides methods to interact with alert popups.
 *
 * @author Sergey Mishanin
 */
@Component
public class WdAlertHandler implements IAlertHandler {
    @Reporter
    private IReporter reporter;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @WaitingDriver
    private IWaitingDriver waitingDriver;

    @Override
    public boolean isDisplayed() {
        return isDisplayed(false);
    }

    @Override
    public boolean isDisplayed(boolean waitForAlert) {
        try {
            if (waitForAlert) {
                waitingDriver.waitForAlertIsPresent();
            }
            return true;
        } catch (Exception ex) {
            reporter.ignoredException(ex);
            return false;
        }
    }

    @Override
    public void accept() {
        Alert alert = getAlert();
        reporter.info("Accept alert [{}]", getText());
        alert.accept();
    }

    @Override
    public void dismiss() {
        Alert alert = getAlert();
        reporter.info("Dismiss alert [{}]", getText());
        alert.dismiss();
    }

    @Override
    public IAlertHandler sendKeys(String keys) {
        Alert alert = getAlert();
        reporter.info("Send [{}] keys to alert [{}]", keys, getText());
        alert.sendKeys(keys);
        return this;
    }

    @Override
    public String getText() {
        return getAlert().getText();
    }

    private Alert getAlert() {
        WebDriver driver = webDriverFactory.getDriver();
        waitingDriver.waitForAlertIsPresent();
        return driver.switchTo().alert();
    }
}
