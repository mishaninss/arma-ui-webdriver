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

import com.github.mishaninss.uidriver.interfaces.IContainerQuietWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IContainerWaitingDriver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdContainerQuietWaitingDriver implements IContainerQuietWaitingDriver {

    private IContainerWaitingDriver containerWaitingDriver;

    public WdContainerQuietWaitingDriver(IContainerWaitingDriver containerWaitingDriver) {
        this.containerWaitingDriver = containerWaitingDriver;
    }

    @Override
    public boolean isVisible() {
        return executeAndSuppressException(() -> containerWaitingDriver.isVisible());
    }

    @Override
    public boolean isVisible(long timeoutInSeconds) {
        return executeAndSuppressException(() -> containerWaitingDriver.isVisible(timeoutInSeconds, ChronoUnit.SECONDS));
    }

    @Override
    public boolean isVisible(long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> containerWaitingDriver.isVisible(timeout, unit));
    }

    @Override
    public boolean allElementsAreVisible() {
        return executeAndSuppressException(() -> containerWaitingDriver.allElementsAreVisible());
    }

    @Override
    public boolean allElementsAreVisible(long timeoutInSeconds) {
        return executeAndSuppressException(() -> containerWaitingDriver.allElementsAreVisible(timeoutInSeconds, ChronoUnit.SECONDS));
    }

    @Override
    public boolean allElementsAreVisible(long timeout, TemporalUnit unit) {
        return executeAndSuppressException(() -> containerWaitingDriver.allElementsAreVisible(timeout, unit));
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
