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

import com.github.mishaninss.html.listeners.ElementEvent;
import com.github.mishaninss.html.listeners.FiresEvent;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IThisElementDriver;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Sergey Mishanin
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdThisElementDriver implements IThisElementDriver {
    @ElementDriver
    private IElementDriver elementDriver;

    private ILocatable element;

    public WdThisElementDriver(ILocatable element){
        this.element = element;
    }

    /**
     * Performs scrolling to make the element visible on screen
     */
    @Override
    public IThisElementDriver scrollTo(){
        elementDriver.scrollToElement(element);
        return this;
    }

    /**
     * Simulates right click on the element
     */
    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver contextClick() {
        elementDriver.contextClickOnElement(element);
        return this;
    }

    /**
     * Checks if the element is displayed on the page or not.
     * @return true if the element exists on the page and displayed; false otherwise.
     */
    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed(){
        return isDisplayed(true);
    }

    /**
     * Checks if the element is displayed on the page or not.
     * @param waitForElement - true if you want to wait for an element existence;
     * false otherwise.
     * @return true if element exists on the page and displayed; false otherwise.
     */
    @Override
    @FiresEvent(ElementEvent.IS_DISPLAYED)
    public boolean isDisplayed(boolean waitForElement){
        return elementDriver.isElementDisplayed(element, waitForElement);
    }

    /**
     * Checks if the element is enabled or not.
     * @return true if the element is enabled; false otherwise.
     */
    @Override
    public boolean isEnabled(){
        return elementDriver.isElementEnabled(element);
    }

    /**
     * Simulates left click on the element
     */
    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver click(){
        elementDriver.clickOnElement(element);
        return this;
    }

    /**
     * Simulates left click on the element without waiting for element is clickable
     */
    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver simpleClick(){
        elementDriver.simpleClickOnElement(element);
        return this;
    }

    /**
     * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
     * @param key - pressed key
     */
    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver clickWithKeyPressed(Keys key){
        elementDriver.clickOnElementWithKeyPressed(element, key);
        return this;
    }

    /**
     * Simulates typing into an element
     * @param keysToSend - keys to send
     */
    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public IThisElementDriver sendKeys(CharSequence... keysToSend){
        elementDriver.sendKeysToElement(element, keysToSend);
        return this;
    }

    /**
     * Can be used for text inputs to clear the current value
     */
    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public IThisElementDriver clear(){
        elementDriver.clearElement(element);
        return this;
    }

    @Override
    public byte[] takeScreenshot(){
        return elementDriver.takeElementScreenshot(element);
    }

    @Override
    public IThisElementDriver highlight(){
        elementDriver.highlightElement(element);
        return this;
    }

    @Override
    public IThisElementDriver unhighlight(){
        elementDriver.unhighlightElement(element);
        return this;
    }

    @Override
    public IThisElementDriver addDebugInfo(final String info, final String tooltip){
        elementDriver.addElementDebugInfo(element, info, tooltip);
        return this;
    }

    @Override
    public IThisElementDriver removeDebugInfo(){
        elementDriver.removeElementDebugInfo();
        return this;
    }

    @Override
    public Point getLocation() {
        return elementDriver.getLocation(element);
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver hover() {
        elementDriver.hoverElement(element);
        return this;
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver clickWithDelayElement() {
        elementDriver.clickWithDelayElement(element);
        return this;
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public Object executeJs(String javaScript){
        return elementDriver.executeJsOnElement(javaScript, element);
    }

    @Override
    @FiresEvent(ElementEvent.ACTION)
    public IThisElementDriver jsClick(){
        executeJs("arguments[0].click()");
        return this;
    }

    @Override
    @FiresEvent(ElementEvent.CHANGE_VALUE)
    public IThisElementDriver setValue(String value){
        elementDriver.setValueToElement(element, value);
        return this;
    }

    @Override
    public ILocatable getElement() {
        return element;
    }
}
