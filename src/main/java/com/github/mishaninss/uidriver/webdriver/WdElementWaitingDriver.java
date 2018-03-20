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

import com.github.mishaninss.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.temporal.TemporalUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementWaitingDriver implements IElementWaitingDriver {

    private ILocatable element;

    @Autowired
    private IWaitingDriver waitingDriver;

    public WdElementWaitingDriver(ILocatable element){
        this.element = element;
    }

    @Override
    public void isVisible(){
        waitingDriver.waitForElementIsVisible(element);
    }

    @Override
    public void isVisible(long timeoutInSeconds){
        waitingDriver.waitForElementIsVisible(element, timeoutInSeconds);
    }

    @Override
    public void isVisible(long timeout, TemporalUnit unit){
        waitingDriver.waitForElementIsVisible(element, timeout, unit);
    }

    @Override
    public void isNotVisible(){
        waitingDriver.waitForElementIsNotVisible(element);
    }

    @Override
    public void isNotVisible(long timeoutInSeconds){
        waitingDriver.waitForElementIsNotVisible(element, timeoutInSeconds);
    }

    @Override
    public void isNotVisible(long timeout, TemporalUnit unit){
        waitingDriver.waitForElementIsNotVisible(element, timeout, unit);
    }

    @Override
    public void isClickable(){
        waitingDriver.waitForElementIsClickable(element);
    }

    @Override
    public void isClickable(long timeoutInSeconds){
        waitingDriver.waitForElementIsClickable(element, timeoutInSeconds);
    }

    @Override
    public void isClickable(long timeout, TemporalUnit unit){
        waitingDriver.waitForElementIsClickable(element, timeout, unit);
    }
}
