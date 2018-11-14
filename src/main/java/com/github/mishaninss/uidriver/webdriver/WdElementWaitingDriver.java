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

import com.github.mishaninss.html.interfaces.IInteractiveElement;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IElementQuietWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.TemporalUnit;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementWaitingDriver implements IElementWaitingDriver {
    @Autowired
    private ApplicationContext applicationContext;

    private IInteractiveElement element;

    @WaitingDriver
    private IWaitingDriver waitingDriver;

    public WdElementWaitingDriver(IInteractiveElement element) {
        this.element = element;
    }

    @Override
    public IElementQuietWaitingDriver quietly() {
        return applicationContext.getBean(IElementQuietWaitingDriver.class, this);
    }

    @Override
    public void isVisible() {
        waitingDriver.waitForElementIsVisible(element);
    }

    @Override
    public void isVisible(long timeoutInSeconds) {
        waitingDriver.waitForElementIsVisible(element, timeoutInSeconds);
    }

    @Override
    public void isVisible(long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementIsVisible(element, timeout, unit);
    }

    @Override
    public void isNotVisible() {
        waitingDriver.waitForElementIsNotVisible(element);
    }

    @Override
    public void isNotVisible(long timeoutInSeconds) {
        waitingDriver.waitForElementIsNotVisible(element, timeoutInSeconds);
    }

    @Override
    public void isNotVisible(long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementIsNotVisible(element, timeout, unit);
    }

    @Override
    public void isClickable() {
        waitingDriver.waitForElementIsClickable(element);
    }

    @Override
    public void isClickable(long timeoutInSeconds) {
        waitingDriver.waitForElementIsClickable(element, timeoutInSeconds);
    }

    @Override
    public void isClickable(long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementIsClickable(element, timeout, unit);
    }

    @Override
    public void attributeToBeNotEmpty(String attribute) {
        waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute);
    }

    @Override
    public void attributeToBeNotEmpty(String attribute, long timeoutInSeconds) {
        waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute, timeoutInSeconds);
    }

    @Override
    public void attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute, timeout, unit);
    }

    @Override
    public void attributeToBe(String attribute, String value) {
        waitingDriver.waitForElementAttributeToBe(element, attribute, value);
    }

    @Override
    public void attributeToBe(String attribute, String value, long timeoutInSeconds) {
        waitingDriver.waitForElementAttributeToBe(element, attribute, value, timeoutInSeconds);
    }

    @Override
    public void attributeToBe(String attribute, String value, long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementAttributeToBe(element, attribute, value, timeout, unit);
    }

    @Override
    public void attributeContains(String attribute, String value) {
        waitingDriver.waitForElementAttributeContains(element, attribute, value);
    }

    @Override
    public void attributeContains(String attribute, String value, long timeoutInSeconds) {
        waitingDriver.waitForElementAttributeContains(element, attribute, value, timeoutInSeconds);
    }

    @Override
    public void attributeContains(String attribute, String value, long timeout, TemporalUnit unit) {
        waitingDriver.waitForElementAttributeContains(element, attribute, value, timeout, unit);
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition) {
        return waitingDriver.waitForCondition(() -> condition.apply(element));
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(element), message);
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds) {
        return waitingDriver.waitForCondition(() -> condition.apply(element), timeoutInSeconds);
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(element), timeoutInSeconds, message);
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit) {
        return waitingDriver.waitForCondition(() -> condition.apply(element), timeout, unit);
    }

    @Override
    public <T> T condition(Function<IInteractiveElement, T> condition, long timeout, TemporalUnit unit, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(element), timeout, unit, message);
    }
}
