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

import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.ISelectElementDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author Sergey Mishanin
 *
 */
@Component
public class WdSelectElementDriver implements ISelectElementDriver {
    @ElementDriver
    private IElementDriver elementDriver;
    @Autowired
    IWebDriverFactory webDriverFactory;

    private Select findSelectElement(String locator){
        WebDriver driver = webDriverFactory.getDriver();
        WebElement element = driver.findElement(LocatorConverter.toBy(locator));
        return new Select(element);
    }

    private Select findSelectElement(ILocatable element){
        return new Select(elementDriver.findElement(element));
    }
   
    @Override
    public WdSelectElementDriver selectByValue(String locator, String value){
        Select select = findSelectElement(locator); 
        select.selectByValue(value);
        return this;
    }

    @Override
    public WdSelectElementDriver selectByValue(ILocatable element, String value){
        Select select = findSelectElement(element);
        select.selectByValue(value);
        return this;
    }
    
    @Override
    public WdSelectElementDriver selectByVisibleText(String locator, String text){
        Select select = findSelectElement(locator); 
        select.selectByVisibleText(text);
        return this;
    }

    @Override
    public WdSelectElementDriver selectByVisibleText(ILocatable element, String text){
        Select select = findSelectElement(element);
        select.selectByVisibleText(text);
        return this;
    }
    
    @Override
    public WdSelectElementDriver selectByIndex(String locator, int index){
        Select select = findSelectElement(locator); 
        select.selectByIndex(index);
        return this;
    }

    @Override
    public WdSelectElementDriver selectByIndex(ILocatable element, int index){
        Select select = findSelectElement(element);
        select.selectByIndex(index);
        return this;
    }

    @Override
    public WdSelectElementDriver deselectAll(String locator) {
        Select select = findSelectElement(locator);
        select.deselectAll();
        return this;
    }

    @Override
    public WdSelectElementDriver deselectAll(ILocatable element) {
        Select select = findSelectElement(element);
        select.deselectAll();
        return this;
    }

    @Override
    public WdSelectElementDriver deselectByValue(String locator, String value) {
        Select select = findSelectElement(locator);
        select.deselectByValue(value);
        return this;
    }

    @Override
    public WdSelectElementDriver deselectByVisibleText(String locator, String text) {
        Select select = findSelectElement(locator);
        select.deselectByVisibleText(text);
        return this;
    }

    @Override
    public WdSelectElementDriver deselectByIndex(String locator, int index) {
        Select select = findSelectElement(locator);
        select.deselectByIndex(index);
        return this;
    }

    @Override
    public String[] getAllSelectedOptions(String locator) {
        Select select = findSelectElement(locator);
        List<WebElement> selectedOptions = select.getAllSelectedOptions();
        return getOptionsText(selectedOptions);
    }

    @Override
    public String[] getOptions(String locator) {
        Select select = findSelectElement(locator);
        List<WebElement> options = select.getOptions();
        return getOptionsText(options);
    }
    
    private String getOptionText(WebElement element){
        return element.getText();
    }
    
    private String[] getOptionsText(List<WebElement> options){
        String[] optionsText = new String[options.size()];
        for (int i=0; i<options.size(); i++){
            optionsText[i] = getOptionText(options.get(i));
        }
        return optionsText;
    }
    
}
