package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementQuietWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementWaitingDriver implements IElementWaitingDriver {

  @Autowired
  private ApplicationContext applicationContext;
  private final IInteractiveElement element;
  private String elementName;
  @Reporter
  private IReporter reporter;

  @WaitingDriver
  private IWaitingDriver waitingDriver;

  public WdElementWaitingDriver(IInteractiveElement element) {
    this.element = element;
    if (element instanceof INamed) {
      elementName = ((INamed) element).getLoggableName();
    } else {
      elementName = element.getLocatorsPath();
    }
  }

  @Override
  public IElementQuietWaitingDriver quietly() {
    return applicationContext.getBean(IElementQuietWaitingDriver.class, this);
  }

  @Override
  public void isVisible() {
    String message = String.format("Ожидаем появления элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsVisible(element);
  }

  @Override
  public void isVisible(long timeoutInSeconds) {
    String message = String.format("Ожидаем появления элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsVisible(element, timeoutInSeconds);
  }

  @Override
  public void isVisible(long timeout, TemporalUnit unit) {
    String message = String.format("Ожидаем появления элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsVisible(element, timeout, unit);
  }

  @Override
  public void isNotVisible() {
    String message = String.format("Ожидаем исчезновения элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsNotVisible(element);
  }

  @Override
  public void isNotVisible(long timeoutInSeconds) {
    String message = String.format("Ожидаем исчезновения элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsNotVisible(element, timeoutInSeconds);
  }

  @Override
  public void isNotVisible(long timeout, TemporalUnit unit) {
    String message = String.format("Ожидаем исчезновения элемента %s", elementName);
    reporter.info(message);
    waitingDriver.waitForElementIsNotVisible(element, timeout, unit);
  }

  @Override
  public void isClickable() {
    waitingDriver.waitForElementIsClickable(element);
  }

  @Override
  public void isClickable(long timeoutInSeconds) {
    waitingDriver.waitForElementIsClickable(element, timeoutInSeconds);
  }

  @Override
  public void isClickable(long timeout, TemporalUnit unit) {
    waitingDriver.waitForElementIsClickable(element, timeout, unit);
  }

  @Override
  public void attributeToBeNotEmpty(String attribute) {
    waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute);
  }

  @Override
  public void attributeToBeNotEmpty(String attribute, long timeoutInSeconds) {
    waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute, timeoutInSeconds);
  }

  @Override
  public void attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit) {
    waitingDriver.waitForElementAttributeToBeNotEmpty(element, attribute, timeout, unit);
  }

  @Override
  public void attributeToBe(String attribute, String value) {
    waitingDriver.waitForElementAttributeToBe(element, attribute, value);
  }

  @Override
  public void attributeToBe(String attribute, String value, long timeoutInSeconds) {
    waitingDriver.waitForElementAttributeToBe(element, attribute, value, timeoutInSeconds);
  }

  @Override
  public void attributeToBe(String attribute, String value, long timeout, TemporalUnit unit) {
    waitingDriver.waitForElementAttributeToBe(element, attribute, value, timeout, unit);
  }

  @Override
  public void attributeContains(String attribute, String value) {
    waitingDriver.waitForElementAttributeContains(element, attribute, value);
  }

  @Override
  public void attributeContains(String attribute, String value, long timeoutInSeconds) {
    waitingDriver.waitForElementAttributeContains(element, attribute, value, timeoutInSeconds);
  }

  @Override
  public void attributeContains(String attribute, String value, long timeout, TemporalUnit unit) {
    waitingDriver.waitForElementAttributeContains(element, attribute, value, timeout, unit);
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition) {
    return waitingDriver.waitForCondition(() -> condition.apply(element));
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition, String message) {
    return waitingDriver.waitForCondition(() -> condition.apply(element), message);
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds) {
    return waitingDriver.waitForCondition(() -> condition.apply(element), timeoutInSeconds);
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition, long timeoutInSeconds,
      String message) {
    return waitingDriver
        .waitForCondition(() -> condition.apply(element), timeoutInSeconds, message);
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition, long timeout,
      TemporalUnit unit) {
    return waitingDriver.waitForCondition(() -> condition.apply(element), timeout, unit);
  }

  @Override
  public <T> T condition(Function<IInteractiveElement, T> condition, long timeout,
      TemporalUnit unit, String message) {
    return waitingDriver.waitForCondition(() -> condition.apply(element), timeout, unit, message);
  }

  @Override
  public void valueToBe(String value) {
    waitingDriver.waitForCondition(() -> StringUtils.equals(element.readValue(), value));
  }

  @Override
  public void valueNotToBe(String value) {
    waitingDriver.waitForCondition(() -> !StringUtils.equals(element.readValue(), value));
  }

  @Override
  public void exists() {
    waitingDriver.waitForElementExists(element);
  }

  @Override
  public void exists(long l) {
    waitingDriver.waitForElementExists(element, l);
  }

  @Override
  public void exists(long l, TemporalUnit temporalUnit) {
    waitingDriver.waitForElementExists(element, l, temporalUnit);
  }
}
