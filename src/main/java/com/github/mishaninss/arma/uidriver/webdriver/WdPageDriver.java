package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.Arma;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IScreenshoter;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.arma.utils.Dimension;
import com.github.mishaninss.arma.utils.UrlUtils;
import java.util.Map;
import java.util.function.BiConsumer;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link com.github.mishaninss.arma.uidriver.interfaces.IPageDriver} interface
 * based on WebDriver engine Provides methods to interact with a page in browser.
 *
 * @author Sergey Mishanin
 */
@Component
public class WdPageDriver implements IPageDriver {

  @Reporter
  private IReporter reporter;
  @Autowired
  private WebDriverProperties properties;
  @Autowired
  private IWebDriverFactory webDriverFactory;
  @Autowired
  private WebElementProvider webElementProvider;
  @WaitingDriver
  private IWaitingDriver waitingDriver;
  @Autowired
  private IScreenshoter screenshoter;
  @Autowired
  private UrlUtils urlUtils;
  @Autowired
  private LocatorConverter locatorConverter;
  @Autowired
  private ApplicationContext applicationContext;

  private boolean inFrame;

  private BiConsumer<String, Arma> postPageOpenMethod;

  @Override
  public boolean isInFrame() {
    return inFrame;
  }

  @Override
  public void setPostPageOpenMethod(BiConsumer<String, Arma> postPageOpenMethod) {
    this.postPageOpenMethod = postPageOpenMethod;
  }

  @Override
  public WdPageDriver goToUrl(String url) {
    String resolvedUrl = urlUtils.resolveUrl(url);
    reporter.info("Open URL " + resolvedUrl);
    WebDriver driver = webDriverFactory.getDriver();
    driver.get(resolvedUrl);
    try {
      waitingDriver.waitForPageUpdate();
    } catch (UnhandledAlertException ex) {
      String unexpectedAlertBehaviour = properties.driver().unexpectedAlertBehaviour;
      if (!StringUtils.equalsAnyIgnoreCase(unexpectedAlertBehaviour, "accept", "dismiss")) {
        throw ex;
      }
    }
    if (postPageOpenMethod != null) {
      postPageOpenMethod.accept(url, applicationContext.getBean(Arma.class));
    }
    return this;
  }

  @Override
  public WdPageDriver refreshPage() {
    webDriverFactory.getDriver().navigate().refresh();
    return this;
  }

  @Override
  public WdPageDriver navigateBack() {
    webDriverFactory.getDriver().navigate().back();
    return this;
  }

  @Override
  public Object executeAsyncJS(String javaScript) {
    WebDriver driver = webDriverFactory.getDriver();
    return ((JavascriptExecutor) driver).executeAsyncScript(javaScript);
  }

  @Override
  public Object executeAsyncJS(String javaScript, Object... args) {
    WebDriver driver = webDriverFactory.getDriver();
    return ((JavascriptExecutor) driver).executeAsyncScript(javaScript, args);
  }

  @Override
  public Object executeAsyncJS(String javaScript, ILocatable element, Object... args) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement webElement = webElementProvider.findElement(element);
    return ((JavascriptExecutor) driver).executeAsyncScript(javaScript, webElement, args);
  }

  @Override
  public Object executeJS(String javaScript) {
    WebDriver driver = webDriverFactory.getDriver();
    return ((JavascriptExecutor) driver).executeScript(javaScript);
  }

  @Override
  public Object executeJS(String javaScript, Map<String, Object> params) {
    WebDriver driver = webDriverFactory.getDriver();
    return ((JavascriptExecutor) driver).executeScript(javaScript, params);
  }

  @Override
  public Object executeJS(String javaScript, String locator, Object... args) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement webElement = driver.findElement(locatorConverter.toBy(locator));
    return ((JavascriptExecutor) driver).executeScript(javaScript, webElement, args);
  }

  @Override
  public String getCurrentUrl() {
    WebDriver driver = webDriverFactory.getDriver();
    String url = driver.getCurrentUrl();
    reporter.debug("Текущий URL {}", url);
    return url;
  }

  @Override
  public String getPageTitle() {
    WebDriver driver = webDriverFactory.getDriver();
    return driver.getTitle();
  }

  @Override
  public byte[] takeScreenshot() {
    return screenshoter.takeScreenshot();
  }

  @Override
  public boolean scrollToBottom() {
    try {
      int innerHeight = Integer.parseInt(executeJS("return document.body.scrollHeight").toString());
      int positionBefore;
      int positionAfter;
      do {
        positionBefore = Integer.parseInt(executeJS("return window.pageYOffset;").toString());
        executeJS("window.scrollBy(0," + innerHeight + ");");
        waitingDriver.waitForPageUpdate();
        positionAfter = Integer.parseInt(executeJS("return window.pageYOffset;").toString());
      } while (positionAfter - positionBefore > 0);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  @Override
  public String getPageSource() {
    return webDriverFactory.getDriver().getPageSource();
  }

  @Override
  public WdPageDriver switchToFrame(String nameOrId) {
    WebDriver driver = webDriverFactory.getDriver();
    driver.switchTo().frame(nameOrId);
    inFrame = true;
    return this;
  }

  @Override
  public WdPageDriver switchToFrame(ILocatable frameElement) {
    WebDriver driver = webDriverFactory.getDriver();
    WebElement webElement = webElementProvider.findElement(frameElement);
    driver.switchTo().frame(webElement);
    inFrame = true;
    return this;
  }

  @Override
  public WdPageDriver switchToDefaultContent() {
    webDriverFactory.getDriver().switchTo().defaultContent();
    inFrame = false;
    return this;
  }

  @Override
  public WdPageDriver scrollToTop() {
    executeJS("window.scrollTo(0,0);");
    return this;
  }

  @Override
  public Dimension getViewportSize() {
    int width = Integer.parseInt(executeJS("return window.innerWidth").toString());
    int height = Integer.parseInt(executeJS("return window.innerHeight").toString());
    return new Dimension(width, height);
  }
}
