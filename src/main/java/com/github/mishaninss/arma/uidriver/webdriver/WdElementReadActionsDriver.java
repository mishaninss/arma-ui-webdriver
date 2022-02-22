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

package com.github.mishaninss.arma.uidriver.webdriver;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.html.listeners.ElementEvent;
import com.github.mishaninss.arma.html.listeners.FiresEvent;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementReadActionDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

/**
 * @author Sergey Mishanin
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementReadActionsDriver implements IElementReadActionDriver {
    @ElementDriver
    private IElementDriver elementDriver;

    private final ILocatable element;

    public WdElementReadActionsDriver(ILocatable element) {
        this.element = element;
    }

    /**
     * Get the value of a the given attribute of the element.
     *
     * @param attribute - name of the attribute
     * @return the value of a the given attribute
     */
    @Override
    @FiresEvent(value = ElementEvent.READ_VALUE, message = "attribute [${1}]")
    public String attribute(String attribute) {
        return elementDriver.getAttributeOfElement(element, attribute);
    }

    /**
     * Get the visible inner text of this element, including sub-elements, without any leading or trailing whitespace.
     *
     * @return The visible inner text of this element.
     */
    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String text() {
        return elementDriver.getTextFromElement(element);
    }

    /**
     * Get the full inner text of this element, including hidden text and text from sub-elements, without any leading or trailing whitespace.
     *
     * @return The full inner text of this element.
     */
    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String fullText() {
        return elementDriver.getFullTextFromElement(element);
    }

    @Override
    @FiresEvent(ElementEvent.READ_VALUE)
    public String tagName() {
        return elementDriver.getTagName(element);
    }

    @Override
    @FiresEvent(value = ElementEvent.READ_VALUE, message = "css value [${1}]")
    public String cssValue(String property) {
        return elementDriver.getCssValue(element, property);
    }

    @Override
    public ILocatable getElement() {
        return element;
    }

    /**
     * Checks if the element is selected or not.
     *
     * @return true if the element is selected; false otherwise.
     */
    @Override
    public boolean isSelected() {
        return elementDriver.isElementSelected(element);
    }
}
