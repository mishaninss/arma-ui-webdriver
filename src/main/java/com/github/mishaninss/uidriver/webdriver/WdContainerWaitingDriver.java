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

import com.github.mishaninss.data.WebDriverProperties;
import com.github.mishaninss.html.interfaces.IElementsContainer;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IContainerQuietWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IContainerWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdContainerWaitingDriver implements IContainerWaitingDriver {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebDriverProperties properties;

    private IElementsContainer container;

    @WaitingDriver
    private IWaitingDriver waitingDriver;

    public WdContainerWaitingDriver(IElementsContainer container) {
        this.container = container;
    }

    @Override
    public IContainerQuietWaitingDriver quietly() {
        return applicationContext.getBean(IContainerQuietWaitingDriver.class, this);
    }

    @Override
    public void isVisible() {
        isVisible(properties.driver().timeoutsElement, ChronoUnit.SECONDS);
    }

    @Override
    public void isVisible(long timeoutInSeconds) {
        isVisible(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void isVisible(long timeout, TemporalUnit unit) {
        if (StringUtils.isNotBlank(container.getLocator())) {
            waitingDriver.waitForElementIsVisible(container, timeout, unit);
        } else {
            allElementsAreVisible(timeout, unit);
        }
    }

    @Override
    public void allElementsAreVisible() {
        allElementsAreVisible(properties.driver().timeoutsElement, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreVisible(long timeoutInSeconds) {
        allElementsAreVisible(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreVisible(long timeout, TemporalUnit unit) {
        container.getElements().values().stream()
                .filter(element -> !element.isOptional())
                .forEach(element -> waitingDriver.waitForElementIsVisible(element, timeout, unit));
    }

    @Override
    public void allElementsAreClickable() {
        allElementsAreClickable(properties.driver().timeoutsElement, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreClickable(long timeoutInSeconds) {
        allElementsAreClickable(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreClickable(long timeout, TemporalUnit unit) {
        container.getElements().values().stream()
                .filter(element -> !element.isOptional())
                .forEach(element -> waitingDriver.waitForElementIsClickable(element, timeout, unit));
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition) {
        return waitingDriver.waitForCondition(() -> condition.apply(container));
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), message);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeoutInSeconds, message);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeoutInSeconds);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeout, unit);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeout, unit, message);
    }
}
