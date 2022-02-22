package com.github.mishaninss.arma.uidriver.webdriver;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.uidriver.LocatorType;
import com.github.mishaninss.arma.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Mishanin
 * Class for locators handling.
 */
@Component
@Primary
public class LocatorConverter {
    /**
     * Parsing engine
     */
    private static final Pattern PATTERN = Pattern.compile("(?:([a-zA-Z]+)\\s*=\\s*)?(.+)");

    /**
     * Locator converters. Pairs "locator type - converter"
     */
    private final Map<String, Function<String, By>> converters = new HashMap<>(Map.of(
            LocatorType.ID, this::byForId,
            LocatorType.NAME, this::byForName,
            LocatorType.XPATH, this::byForXPath,
            LocatorType.CSS, this::byForCSS,
            LocatorType.LINK, this::byForLink,
            LocatorType.PARTIAL_LINK, this::byForPartialLink,
            LocatorType.TAG, this::byForTag,
            LocatorType.CLASS, this::byForClass
    ));

    public LocatorConverter() {
    }

    /**
     * Templates for error messages
     */
    private static final String UNKNOWN_TYPE = "unknown type of locator \"%1$s\"";
    private static final String INDEXED_LOCATOR_PATTERN = "(#)(\\d+)(#)(.*)";

    public Object[] checkForIndex(String locator) {
        if (StringUtils.isBlank(locator)) {
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

    public int getArgsCountInLocatorTemplate(String locator) {
        if (!locator.contains("%")) {
            return 0;
        }
        return StringUtils.countMatches(locator, "%");
    }

    public Pair<String, String> parseLocator(String locator) {
        Preconditions.checkNotBlank(locator, "locator");

        String locatorValue = locator.trim();
        if (locatorValue.length() == 0) {
            throw new IllegalArgumentException();
        }

        String locatorType = null;
        Matcher matcher = PATTERN.matcher(locatorValue);
        if (matcher.matches()) {
            int index = 0;
            locatorType = matcher.group(++index);
            if (locatorType != null) {
                locatorType = locatorType.toLowerCase();
                locatorValue = matcher.group(++index);
            } else {
                locatorType = detectImplicitType(locatorValue);
            }
        }
        return Pair.of(locatorType, locatorValue);
    }

    public By toBy(final WebElement webElement) {
        String stringWebElement = webElement.toString();
        String[] tokens = stringWebElement.split("->");
        String locator = tokens[1];
        locator = StringUtils.stripEnd(locator.replaceFirst(":", "="), "]").trim();
        return toBy(locator);
    }

    /**
     * Converts locator to "location technique" for WebDriver API
     *
     * @param locator - locator to be converted
     * @return locator as By
     */
    public By toBy(final String locator) {
        Preconditions.checkNotBlank(locator, "locator");

        String locatorValue = locator.trim();
        if (locatorValue.length() == 0) {
            throw new IllegalArgumentException();
        }

        String locatorType = null;
        Matcher matcher = PATTERN.matcher(locatorValue);
        if (matcher.matches()) {
            int index = 0;
            locatorType = matcher.group(++index);
            if (locatorType != null) {
                locatorType = locatorType.toLowerCase();
                locatorValue = matcher.group(++index);
            } else {
                locatorType = detectImplicitType(locatorValue);
            }
        }

        Function<String, By> locatorConverter = converters.get(locatorType);
        if (locatorConverter == null) {
            String errorMessage = String.format(UNKNOWN_TYPE, locator);
            throw new IllegalArgumentException(errorMessage);
        }

        return locatorConverter.apply(locatorValue);
    }

    /**
     * Determines type of locator by its value.
     * Uses the following strategy:
     * <ul>
     * <li>dom, for locators starting with "document."</li>
     * <li>xpath, for locators starting with "//" or "(//"</li>
     * <li>identifier, otherwise</li>
     * </ul>
     *
     * @param locatorValue - locator value without explicit type
     */
    private String detectImplicitType(final String locatorValue) {
        String implicitType = "";
        if (locatorValue.startsWith("./") || locatorValue.startsWith("//") || locatorValue.startsWith("(//") || locatorValue.startsWith("(./")) {
            implicitType = LocatorType.XPATH;
        }
        return implicitType;
    }

    /**
     * Converter for type "id"
     */
    public By byForId(final String locator) {
        return By.id(locator);
    }

    /**
     * Converter for type "name"
     */
    public By byForName(final String locator) {
        return By.name(locator);
    }

    /**
     * Converter for type "xpath"
     */
    public By byForXPath(final String locator) {
        return By.xpath(locator);
    }

    /**
     * Converter for type "link"
     */
    public By byForLink(final String locatorValue) {
        return By.linkText(locatorValue);
    }

    /**
     * Converter for type "partialLink"
     */
    public By byForPartialLink(final String locatorValue) {
        return By.partialLinkText(locatorValue);
    }

    /**
     * Converter for type "tagName"
     */
    public By byForTag(final String locatorValue) {
        return By.tagName(locatorValue);
    }

    /**
     * Converter for type "className"
     */
    public By byForClass(final String locatorValue) {
        return By.className(locatorValue);
    }

    /**
     * Converter for type "css"
     */
    public By byForCSS(final String locatorValue) {
        return By.cssSelector(locatorValue);
    }

    public void addConverter(String locatorKey, Function<String,By> converter){
        converters.put(locatorKey, converter);
    }
}