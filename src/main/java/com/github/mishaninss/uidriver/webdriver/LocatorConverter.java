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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for locators handling. 
 */
public final class LocatorConverter {
    /**
     * Parsing engine
     */
    private static final Pattern pattern;
    /**
     * Locator converters. Pairs "locator type - converter"
     */
    private static final Map<String, ByFor> converters;
    /**
     * Templates for error messages
     */
    private static final String UNKNOWN_TYPE = "unknown type of locator \"%1$s\"";
    private static final String INDEXED_LOCATOR_PATTERN = "(#)(\\d+)(#)(.*)";

    /**
     * Hidden constructor
     */
    private LocatorConverter() {
    }

    public static Object[] checkForIndex(String locator) {
        if (StringUtils.isBlank(locator)){
            return new Object[0];
        }
        Pattern p = Pattern.compile(INDEXED_LOCATOR_PATTERN);
        Matcher m = p.matcher(locator);
        if (m.find()) {
            Object[] result = new Object[2];
            result[0] = Integer.parseInt(m.group(2));
            result[1] = m.group(4);
            return result;
        }
        return new Object[0];
    }

    public static int getArgsCountInLocatorTemplate(String locator) {
        if (!locator.contains("%")) {
            return 0;
        }
        return StringUtils.countMatches(locator, "%");
    }

    public static By toBy(final WebElement webElement){
        String stringWebElement = webElement.toString();
        String[] tokens = stringWebElement.split("->");
        String locator = tokens[1];
        locator = StringUtils.stripEnd(locator.replaceFirst(":", "="), "]").trim();
        return toBy(locator);
    }
    /** 
     * Converts locator to "location technique" for WebDriver API
     * @param locator - locator to be converted
     * @return locator as By
     */
    public static By toBy(final String locator) {
        if(locator == null) {
            throw new IllegalArgumentException();
        }

        String locatorValue = locator.trim();
        if(locatorValue.length() == 0) {
            throw new IllegalArgumentException();
        }

        String locatorType = null;
        Matcher matcher = pattern.matcher(locatorValue);
        if(matcher.matches()) {
            int index = 0;
            locatorType = matcher.group(++index);
            if(locatorType != null) {
                locatorType = locatorType.toLowerCase();
                locatorValue = matcher.group(++index);
            }
            else {
                locatorType = detectImplicitType(locatorValue);
            }
        }

        ByFor locatorConverter = converters.get(locatorType);
        if(locatorConverter == null) {
            String errorMessage = String.format(UNKNOWN_TYPE, locator);
            throw new IllegalArgumentException(errorMessage);
        }

        return locatorConverter.toBy(locatorValue);
    }

    /** 
     * Determines type of locator by its value.
     * Uses the following strategy:
     * <ul>
     * <li>dom, for locators starting with "document."</li>
     * <li>xpath, for locators starting with "//" or "(//"</li>
     * <li>identifier, otherwise</li>
     * </ul>
     * @param locatorValue - locator value without explicit type
     */
    private static String detectImplicitType(final String locatorValue)
    {
        String implicitType = LocatorType.IDENTIFIER; 
        if(locatorValue.startsWith("./") ||locatorValue.startsWith("//") || locatorValue.startsWith("(//") || locatorValue.startsWith("(./"))
        {
            implicitType = LocatorType.XPATH;
        }
        else if(locatorValue.startsWith("document."))
        {
            implicitType = LocatorType.DOM;
        }
        return implicitType;
    }
    
    /** Converter from locator value to location technique for WebDriver API */
    private interface ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        By toBy(final String locatorValue);
    }

    /** Converter for type "id" */
    private static class ByForId implements ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.id(locatorValue);
        }
    }

    /** Converter for type "name" */
    private static class ByForName implements ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.name(locatorValue);
        }
    }

    /** Converter for type "xpath" */
    private static class ByForXPath implements ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.xpath(locatorValue);
        }
    }

    /** Converter for type "link" */
    private static class ByForLinkText implements ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.linkText(locatorValue);
        }
    }

    /** Converter for type "link" */
    private static class ByForTagName implements ByFor
    {
        /**
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.tagName(locatorValue);
        }
    }

    /** Converter for type "css" */
    private static class ByForCSS implements ByFor
    {
        /** 
         * Converts locator value to "location technique" for WebDriver API
         * @param locatorValue - locator value without explicit type
         */
        @Override
        public By toBy(final String locatorValue)
        {
            return By.cssSelector(locatorValue);
        }
    }

    static
    {
        pattern = Pattern.compile("(?:([a-zA-Z]+)\\s*=\\s*)?(.+)");

        converters = new Hashtable<>();
        converters.put(LocatorType.ID, new ByForId());
        converters.put(LocatorType.NAME, new ByForName());
        converters.put(LocatorType.XPATH, new ByForXPath());
        converters.put(LocatorType.CSS, new ByForCSS());
        converters.put(LocatorType.LINK, new ByForLinkText());
        converters.put(LocatorType.TAG, new ByForTagName());
    }
}
