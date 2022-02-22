package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.ISelectElementDriver;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Mishanin
 */
@Component
public class WdSelectElementDriver implements ISelectElementDriver {

  @Autowired
  private WebElementProvider webElementProvider;
  @Autowired
  IWebDriverFactory webDriverFactory;
  @Autowired
  private LocatorConverter locatorConverter;

  private Select findSelectElement(String locator) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement element = driver.findElement(locatorConverter.toBy(locator));
    return new Select(element);
  }

  private Select findSelectElement(ILocatable element) {
    return new Select(webElementProvider.findElement(element));
  }

  @Override
  public WdSelectElementDriver selectByValue(String locator, String value) {
    Select select = findSelectElement(locator);
    select.selectByValue(value);
    return this;
  }

  @Override
  public WdSelectElementDriver selectByValue(ILocatable element, String value) {
    Select select = findSelectElement(element);
    select.selectByValue(value);
    return this;
  }

  @Override
  public WdSelectElementDriver selectByVisibleText(String locator, String text) {
    Select select = findSelectElement(locator);
    select.selectByVisibleText(text);
    return this;
  }

  @Override
  public WdSelectElementDriver selectByVisibleText(ILocatable element, String text) {
    Select select = findSelectElement(element);
    select.selectByVisibleText(text);
    return this;
  }

  @Override
  public WdSelectElementDriver selectByIndex(String locator, int index) {
    Select select = findSelectElement(locator);
    select.selectByIndex(index);
    return this;
  }

  @Override
  public WdSelectElementDriver selectByIndex(ILocatable element, int index) {
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
  public String[] getAllSelectedOptions(ILocatable element) {
    Select select = findSelectElement(element);
    List<WebElement> selectedOptions = select.getAllSelectedOptions();
    return getOptionsText(selectedOptions);
  }

  @Override
  public String[] getOptions(String locator) {
    Select select = findSelectElement(locator);
    List<WebElement> options = select.getOptions();
    return getOptionsText(options);
  }

  @Override
  public String[] getOptions(ILocatable element) {
    Select select = findSelectElement(element);
    List<WebElement> options = select.getOptions();
    return getOptionsText(options);
  }

  private String getOptionText(WebElement element) {
    return element.getText();
  }

  private String[] getOptionsText(List<WebElement> options) {
    String[] optionsText = new String[options.size()];
    for (int i = 0; i < options.size(); i++) {
      optionsText[i] = getOptionText(options.get(i));
    }
    return optionsText;
  }

}
