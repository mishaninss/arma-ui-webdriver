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

import com.github.mishaninss.uidriver.interfaces.IElementQuietWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.TemporalUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementQuietWaitingDriver implements IElementQuietWaitingDriver {

    private IElementWaitingDriver elementWaitingDriver;

    public WdElementQuietWaitingDriver(IElementWaitingDriver elementWaitingDriver) {
        this.elementWaitingDriver = elementWaitingDriver;
    }

    @Override
    public boolean isVisible() {
        return executeAndSuppressException(() -> elementWaitingDriver.isVisible());
    }

    @Override
    public boolean isVisible(long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.isVisible(timeoutInSeconds));
    }

    @Override
    public boolean isVisible(long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.isVisible(timeout, unit));
    }

    @Override
    public boolean isNotVisible() {
        return executeAndSuppressException(() -> elementWaitingDriver.isNotVisible());
    }

    @Override
    public boolean isNotVisible(long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.isNotVisible(timeoutInSeconds));
    }

    @Override
    public boolean isNotVisible(long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.isNotVisible(timeout, unit));
    }

    @Override
    public boolean isClickable() {
        return executeAndSuppressException(() -> elementWaitingDriver.isClickable());
    }

    @Override
    public boolean isClickable(long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.isClickable(timeoutInSeconds));
    }

    @Override
    public boolean isClickable(long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.isClickable(timeout, unit));
    }

    @Override
    public boolean attributeToBeNotEmpty(String attribute) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBeNotEmpty(attribute));
    }

    @Override
    public boolean attributeToBeNotEmpty(String attribute, long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBeNotEmpty(attribute, timeoutInSeconds));
    }

    @Override
    public boolean attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBeNotEmpty(attribute, timeout, unit));
    }

    @Override
    public boolean attributeToBe(String attribute, String value) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBe(attribute, value));
    }

    @Override
    public boolean attributeToBe(String attribute, String value, long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBe(attribute, value, timeoutInSeconds));
    }

    @Override
    public boolean attributeToBe(String attribute, String value, long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeToBe(attribute, value, timeout, unit));
    }

    @Override
    public boolean attributeContains(String attribute, String value) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeContains(attribute, value));
    }

    @Override
    public boolean attributeContains(String attribute, String value, long timeoutInSeconds) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeContains(attribute, value, timeoutInSeconds));
    }

    @Override
    public boolean attributeContains(String attribute, String value, long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> elementWaitingDriver.attributeContains(attribute, value, timeout, unit));
    }

    private boolean executeAndSuppressException(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
