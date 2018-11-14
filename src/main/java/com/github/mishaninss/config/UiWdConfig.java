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

package com.github.mishaninss.config;

import com.github.mishaninss.aspects.SeleniumAspects;
import com.github.mishaninss.aspects.UiDriverAspects;
import com.github.mishaninss.uidriver.annotations.AlertHandler;
import com.github.mishaninss.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.uidriver.annotations.PageDriver;
import com.github.mishaninss.uidriver.annotations.SelectElementDriver;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.uidriver.interfaces.ISelectElementDriver;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.uidriver.webdriver.WdAlertHandler;
import com.github.mishaninss.uidriver.webdriver.WdBrowserDriver;
import com.github.mishaninss.uidriver.webdriver.WdElementDriver;
import com.github.mishaninss.uidriver.webdriver.WdElementsDriver;
import com.github.mishaninss.uidriver.webdriver.WdPageDriver;
import com.github.mishaninss.uidriver.webdriver.WdSelectElementDriver;
import com.github.mishaninss.uidriver.webdriver.WdWaitingDriver;
import org.aspectj.lang.Aspects;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UiCommonsConfig.class)
public class UiWdConfig {

    @Bean(autowire = Autowire.BY_TYPE)
    public SeleniumAspects seleniumAspects() {
        return Aspects.aspectOf(SeleniumAspects.class);
    }

    @Bean
    public UiDriverAspects uiDriverAspects() {
        return Aspects.aspectOf(UiDriverAspects.class);
    }

    @Bean(IWaitingDriver.QUALIFIER) @WaitingDriver
    public IWaitingDriver waitingDriver(){
        return new WdWaitingDriver();
    }

    @Bean(IBrowserDriver.QUALIFIER) @BrowserDriver
    public IBrowserDriver browserDriver(){
        return new WdBrowserDriver();
    }

    @Bean(IElementDriver.QUALIFIER) @ElementDriver
    public IElementDriver elementDriver(){
        return new WdElementDriver();
    }

    @Bean(IElementsDriver.QUALIFIER) @ElementsDriver
    public IElementsDriver elementsDriver(){
        return new WdElementsDriver();
    }

    @Bean(IPageDriver.QUALIFIER) @PageDriver
    public IPageDriver pageDriver(){
        return new WdPageDriver();
    }

    @Bean(ISelectElementDriver.QUALIFIER) @SelectElementDriver
    public ISelectElementDriver selectElementDriver(){
        return new WdSelectElementDriver();
    }

    @Bean(IAlertHandler.QUALIFIER) @AlertHandler
    public IAlertHandler alertHandler(){
        return new WdAlertHandler();
    }

}
