package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.PageDriver;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IActionsChain;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IPoint;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.arma.utils.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Mishanin
 */
@Component
public class WdElementDriver implements IElementDriver {

  @Reporter
  @Lazy
  protected IReporter reporter;
  @Autowired
  @Lazy
  protected WebDriverProperties properties;
  @Autowired
  @Lazy
  protected IWebDriverFactory webDriverFactory;
  @WaitingDriver
  @Lazy
  protected IWaitingDriver waitingDriver;
  @PageDriver
  @Lazy
  protected IPageDriver pageDriver;
  @Autowired
  @Lazy
  protected WebElementProvider webElementProvider;
  @Autowired
  protected ApplicationContext applicationContext;

  /**
   * Performs scrolling to make the element visible on screen
   *
   * @param element - locatable element
   */
  @Override
  public void scrollIntoView(@NonNull ILocatable element, boolean alignToTop) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement webElement = webElementProvider.findElement(element);
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView(" + alignToTop + ")", webElement);
  }

  /**
   * Performs scrolling to make the element visible on screen
   *
   * @param element - locatable element
   */
  @Override
  public void scrollIntoViewIfNeeded(@NonNull ILocatable element, boolean alignToTop) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement webElement = webElementProvider.findElement(element);
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoViewIfNeeded(" + alignToTop + ")", webElement);
  }

  /**
   * Performs scrolling to make the element visible on screen
   *
   * @param element - locatable elemen
   */
  @Override
  public WdElementDriver scrollToElement(@NonNull ILocatable element) {
    scrollIntoViewIfNeeded(element, false);
    return this;
  }

  /**
   * Simulates right click on the element
   *
   * @param element - locatable element
   */
  @Override
  public IElementDriver contextClickOnElement(@NonNull ILocatable element) {
    waitingDriver.waitForElementIsClickable(element);
    applicationContext.getBean(IActionsChain.class)
        .contextClick(element)
        .perform();
    return this;
  }

  /**
   * Checks if the element is displayed on the page or not.
   *
   * @param element - locator of the element.
   * @return true if the element exists on the page and displayed; false otherwise.
   */
  @Override
  public boolean isElementDisplayed(@NonNull ILocatable element) {
    return isElementDisplayed(element, true);
  }

  /**
   * Checks if element with specified locator is displayed on the page or not.
   *
   * @param element        - locator of the element.
   * @param waitForElement - true if you want to wait for an element existence; false otherwise.
   * @return true if element exists on the page and displayed; false otherwise.
   */
  @Override
  public boolean isElementDisplayed(@NonNull ILocatable element, boolean waitForElement) {
    long timeout = waitForElement ? properties.driver().timeoutsElement : 0;
    try {
      waitingDriver.waitForElementIsVisible(element, timeout, ChronoUnit.MILLIS);
      return true;
    } catch (NoSuchElementException | TimeoutException ex) {
      reporter.ignoredException(ex);
      return false;
    }
  }

  /**
   * Checks if the element is enabled or not.
   *
   * @param element - locator of the element.
   * @return true if the element is enabled; false otherwise.
   */
  @Override
  public boolean isElementEnabled(@NonNull ILocatable element) {
    return webElementProvider.findElement(element).isEnabled();
  }

  /**
   * Checks if the element is selected or not.
   *
   * @param element - locator of the element.
   * @return true if the element is selected; false otherwise.
   */
  @Override
  public boolean isElementSelected(@NonNull ILocatable element) {
    return webElementProvider.findElement(element).isSelected();
  }

  /**
   * Get the value of a the given attribute of the element.
   *
   * @param element   - locator of the element
   * @param attribute - name of the attribute
   * @return the value of a the given attribute
   */
  @Override
  public String getAttributeOfElement(@NonNull ILocatable element, String attribute) {
    return webElementProvider.findElement(element).getAttribute(attribute);
  }

  /**
   * Simulates left click on the element
   *
   * @param element - locator of the element
   */
  @Override
  public WdElementDriver clickOnElement(@NonNull ILocatable element) {
    WebElement webElement = webElementProvider.findElement(element);
    try {
      webElement.click();
    } catch (WebDriverException ex) {
      String message = ex.getMessage();
      if (StringUtils.isNotBlank(message) && message.contains("is not clickable")) {
        reporter.warn("Сaught element is not clickable exception. Will try JS click");
        executeJsOnElement("arguments[0].click()", element);
      } else {
        throw ex;
      }
    }
    return this;
  }

  /**
   * Simulates left click on the element without waiting for element is clickable
   *
   * @param element - locator of the element
   */
  @Override
  public WdElementDriver simpleClickOnElement(@NonNull ILocatable element) {
    webElementProvider.findElement(element).click();
    return this;
  }

  /**
   * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
   *
   * @param element - locator of the element
   * @param key     - pressed key
   */
  @Override
  public WdElementDriver clickOnElementWithKeyPressed(@NonNull ILocatable element,
      CharSequence key) {
    waitingDriver.waitForElementIsClickable(element);
    applicationContext.getBean(IActionsChain.class)
        .keyDown(key)
        .click(element)
        .keyUp(key)
        .perform();
    return this;
  }

  /**
   * Get the visible inner text of this element, including sub-elements, without any leading or
   * trailing whitespace.
   *
   * @param element - locator of the element
   * @return The visible inner text of this element.
   */
  @Override
  public String getTextFromElement(@NonNull ILocatable element) {
    return webElementProvider.findElement(element).getText();
  }

  /**
   * Get the full inner text of this element, including hidden text and text from sub-elements,
   * without any leading or trailing whitespace.
   *
   * @param element - locator of the element
   * @return The full inner text of this element.
   */
  @Override
  public String getFullTextFromElement(@NonNull ILocatable element) {
    return webElementProvider.findElement(element).getAttribute("textContent");
  }

  /**
   * Simulates typing into an element
   *
   * @param element    - locator of the element
   * @param keysToSend - keys to send
   */
  @Override
  public WdElementDriver sendKeysToElement(@NonNull ILocatable element,
      CharSequence... keysToSend) {
    webElementProvider.findElement(element).sendKeys(keysToSend);
    return this;
  }

  /**
   * Can be used for text inputs to clear the current value
   *
   * @param element - locator of the element
   */
  @Override
  public WdElementDriver clearElement(@NonNull ILocatable element) {
    webElementProvider.findElement(element).clear();
    return this;
  }

  @Override
  public byte[] takeElementScreenshot(@NonNull ILocatable element) {
    var webElement = webElementProvider.findElement(element);

    // Get entire page screenshot
    byte[] bytes = pageDriver.takeScreenshot();
    BufferedImage fullImg;
    try (InputStream is = new ByteArrayInputStream(bytes)) {
      fullImg = ImageIO.read(is);
    } catch (Exception ex) {
      return new byte[0];
    }

    // Get the location of element on the page
    var point = webElement.getLocation();

    // Get width and height of the element
    int eleWidth = webElement.getSize().getWidth();
    int eleHeight = webElement.getSize().getHeight();

    // Crop the entire page screenshot to get only element screenshot
    BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(),
        eleWidth, eleHeight);
    try (var baos = new ByteArrayOutputStream()) {
      ImageIO.write(eleScreenshot, "png", baos);
      return baos.toByteArray();
    } catch (IOException e) {
      return new byte[0];
    }
  }

  @Override
  public void clearCache() {
    webElementProvider.clearCache();
  }

  @Override
  public void highlightElement(@NonNull ILocatable element) {
    try {
      WebElement webElement = webElementProvider.findElement(element);
      highlightElement(webElement);
    } catch (Exception ex) {
      reporter.ignoredException(ex);
    }
  }

  private void highlightElement(@NonNull WebElement webElement) {
    WebDriver webDriver = webDriverFactory.getDriver();
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    js.executeScript("arguments[0].style.border='5px solid red'", webElement);
  }

  @Override
  public void unhighlightElement(@NonNull ILocatable element) {
    WebElement webElement = webElementProvider.findElement(element);
    unhighlightElement(webElement);
  }

  private void unhighlightElement(@NonNull WebElement webElement) {
    var webDriver = webDriverFactory.getDriver();
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    js.executeScript("arguments[0].style.border='';", webElement);
  }

  @Override
  public void addElementDebugInfo(@NonNull ILocatable element, final String info,
      final String tooltip) {
    var driver = webDriverFactory.getDriver();
    var webElement = webElementProvider.findElement(element);
    ((JavascriptExecutor) driver).executeScript(
        "var node = document.getElementById('wdDebugInfo');"
            + "if (!node){"
            + "node = document.createElement('span');"
            + "node.id = 'wdDebugInfo';"
            + "node.style.position = 'fixed';"
            + "node.style.zIndex = '9999999';"
            + "node.style.color = 'white';"
            + "node.style.background = 'red';"
            + "node.style['font-weight'] = 'bold';"
            + "node.style['font-size'] = '10pt';"
            + "document.body.appendChild(node);}"
            + "node.innerHTML = arguments[0];"
            + "node.title = arguments[1];"
            + "node.style.display = 'block';"
            + "var elemRect = arguments[2].getBoundingClientRect();"
            + "var nodeRect = node.getBoundingClientRect();"
            + "var maxRight = window.innerWidth - nodeRect.width - 5;"
            + "var maxTop = elemRect.y - nodeRect.height - 5;"
            + "node.style.left = window.innerWidth < elemRect.x + nodeRect.width ? maxRight < 0 ? 0 + 'px': maxRight + 'px' : elemRect.x + 'px';"
            + "node.style.top = maxTop > 0 ? maxTop + 'px' : (elemRect.y + elemRect.height + 5) + 'px';"
        , info, tooltip, webElement);
  }

  @Override
  public void removeElementDebugInfo() {
    var driver = webDriverFactory.getDriver();
    ((JavascriptExecutor) driver).executeScript(
        "var node = document.getElementById('wdDebugInfo');" +
            "if (node) {node.style.display = 'none'}");
  }

  @Override
  public String getTagName(@NonNull ILocatable element) {
    return webElementProvider.findElement(element).getTagName();
  }

  @Override
  public String getCssValue(@NonNull ILocatable element, String cssValue) {
    return webElementProvider.findElement(element).getCssValue(cssValue);
  }

  @Override
  public IPoint getLocation(@NonNull ILocatable element) {
    return new WdPoint(webElementProvider.findElement(element).getLocation());
  }

  @Override
  public IElementDriver hoverElement(@NonNull ILocatable element) {
    applicationContext.getBean(IActionsChain.class)
        .moveToElement(element)
        .perform();
    return this;
  }

  @Override
  public IElementDriver clickWithDelayElement(@NonNull ILocatable element) {
    applicationContext.getBean(IActionsChain.class)
        .clickAndHold(element)
        .pause(Duration.ofSeconds(2))
        .release()
        .perform();
    return this;
  }

  @Override
  public Object executeJsOnElement(@NonNull String javaScript, @NonNull ILocatable element) {
    WebElement webElement = webElementProvider.findElement(element);
    return ((JavascriptExecutor) webDriverFactory.getDriver())
        .executeScript(javaScript, webElement);
  }

  @Override
  public IElementDriver setValueToElement(@NonNull ILocatable element, String value) {
    return setAttributeOfElement(element, "value", value);
  }

  @Override
  public IElementDriver setAttributeOfElement(@NonNull ILocatable element,
      @NonNull String attribute, String value) {
    String js = String.format("arguments[0].setAttribute(\"%s\",\"%s\")", attribute, value);
    executeJsOnElement(js, element);
    return this;
  }

  @Override
  public void removeAttributeOfElement(ILocatable element, String attribute) {
    String js = String.format("arguments[0].removeAttribute(\"%s\")", attribute);
    executeJsOnElement(js, element);
  }

  @Override
  public boolean hasAttribute(ILocatable element, String attribute) {
    return StringUtils.isNotBlank(getAttributeOfElement(element, attribute));
  }

  public WebElement findElement(@NonNull By by) {
    return webDriverFactory.getDriver().findElement(by);
  }

  @Override
  public Dimension getSize(ILocatable element) {
    org.openqa.selenium.Dimension dim = webElementProvider.findElement(element).getSize();
    return new Dimension(dim.getWidth(), dim.getHeight());
  }
}
