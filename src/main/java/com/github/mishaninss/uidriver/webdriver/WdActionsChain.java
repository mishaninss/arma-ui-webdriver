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

import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IActionsChain;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdActionsChain implements IActionsChain {
    @Autowired
    private WebElementProvider webElementProvider;
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @Reporter
    private IReporter reporter;
    @Autowired
    private IWebDriverFactory webDriverFactory;

    private Actions actions;
    private StringBuilder logEntry;

    public WdActionsChain() {
        logEntry = new StringBuilder("Perform actions chain");
    }

    @PostConstruct
    private void init() {
        actions = new Actions(webDriverFactory.getDriver());
    }

    private void addActionLog(String actionLog) {
        logEntry
                .append("\n").append(actionLog);
    }

    private void addActionLog(String actionLog, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof ILocatable) {
                args[i] = INamed.getLoggableNameIfApplicable(arg);
            }
        }
        logEntry.append("\n").append(String.format(actionLog, args));
    }

    @Override
    public IActionsChain click(ILocatable element) {
        WebElement webElement = webElementProvider.findElement(element);
        actions.click(webElement);
        addActionLog("Click on %s", element);
        return this;
    }

    @Override
    public IActionsChain moveToElement(ILocatable element) {
        WebElement webElement = webElementProvider.findElement(element);
        actions.moveToElement(webElement);
        addActionLog("Move to %s", element);
        return this;
    }

    @Override
    public IActionsChain moveToElement(ILocatable element, int xOffset, int yOffset) {
        WebElement webElement = webElementProvider.findElement(element);
        actions.moveToElement(webElement, xOffset, yOffset);
        addActionLog("Move to %s with offset (%d, %d)", element, xOffset, yOffset);
        return this;
    }

    @Override
    public IActionsChain pause(long pause) {
        actions.pause(pause);
        addActionLog("Pause for %d milliseconds", pause);
        return this;
    }

    @Override
    public IActionsChain pause(Duration duration) {
        actions.pause(duration);
        addActionLog("Pause for %s", duration.toString());
        return this;
    }

    @Override
    public IActionsChain keyDown(CharSequence key) {
        actions.keyDown(key);
        addActionLog("Key down [%s]", key);
        return this;
    }

    @Override
    public IActionsChain keyDown(ILocatable target, CharSequence key) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.keyDown(webElement, key);
        addActionLog("Key down [%s] on %s", key, target);
        return this;
    }

    @Override
    public IActionsChain keyUp(CharSequence key) {
        actions.keyUp(key);
        addActionLog("Key up [%s]", key);
        return this;
    }

    @Override
    public IActionsChain keyUp(ILocatable target, CharSequence key) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.keyDown(webElement, key);
        addActionLog("Key up [%s] on %s", key, target);
        return this;
    }

    @Override
    public IActionsChain sendKeys(CharSequence... keys) {
        actions.sendKeys(keys);
        addActionLog("Send keys %s", Arrays.toString(keys));
        return this;
    }

    @Override
    public IActionsChain sendKeys(ILocatable target, CharSequence... keys) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.sendKeys(webElement, keys);
        addActionLog("Send keys %s to %s", Arrays.toString(keys), target);
        return this;
    }

    @Override
    public IActionsChain clickAndHold(ILocatable target) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.clickAndHold(webElement);
        addActionLog("Click on %s and hold", target);
        return this;
    }

    @Override
    public IActionsChain clickAndHold() {
        actions.clickAndHold();
        addActionLog("Click and hold");
        return this;
    }

    @Override
    public IActionsChain release(ILocatable target) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.release(webElement);
        addActionLog("Release on %s", target);
        return this;
    }

    @Override
    public IActionsChain release() {
        actions.release();
        addActionLog("Release");
        return this;
    }

    @Override
    public IActionsChain click() {
        actions.click();
        addActionLog("Click");
        return this;
    }

    @Override
    public IActionsChain doubleClick(ILocatable target) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.doubleClick(webElement);
        addActionLog("Double click on %s", target);
        return this;
    }

    @Override
    public IActionsChain doubleClick() {
        actions.doubleClick();
        addActionLog("Double click");
        return this;
    }

    @Override
    public IActionsChain moveByOffset(int xOffset, int yOffset) {
        actions.moveByOffset(xOffset, yOffset);
        addActionLog("Move by offset (%d, %d)", xOffset, yOffset);
        return this;
    }

    @Override
    public IActionsChain contextClick(ILocatable target) {
        WebElement webElement = webElementProvider.findElement(target);
        actions.contextClick(webElement);
        addActionLog("Context click on %s", target);
        return this;
    }

    @Override
    public IActionsChain contextClick() {
        actions.contextClick();
        addActionLog("Context click");
        return this;
    }

    @Override
    public IActionsChain dragAndDrop(ILocatable source, ILocatable target) {
        WebElement sourceWebElement = webElementProvider.findElement(source);
        WebElement targetWebElement = webElementProvider.findElement(target);
        actions.dragAndDrop(sourceWebElement, targetWebElement);
        addActionLog("Drag and drop from %s to %s", source, target);
        return this;
    }

    @Override
    public IActionsChain dragAndDropBy(ILocatable source, int xOffset, int yOffset) {
        WebElement sourceWebElement = webElementProvider.findElement(source);
        actions.dragAndDropBy(sourceWebElement, xOffset, yOffset);
        addActionLog("Drag and drop %s by offset (%d, %d)", source, xOffset, yOffset);
        return this;
    }

    @Override
    public IActionsChain build() {
        actions.build();
        return this;
    }

    @Override
    public IActionsChain perform() {
        reporter.info(logEntry.toString());
        waitingDriver.waitForPageUpdate();
        actions.perform();
        waitingDriver.waitForPageUpdate();
        return this;
    }
}
