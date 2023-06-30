package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.uidriver.interfaces.IElementQuietWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementWaitingDriver;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementQuietWaitingDriver implements IElementQuietWaitingDriver {

  private final IElementWaitingDriver elementWaitingDriver;

  public WdElementQuietWaitingDriver(IElementWaitingDriver elementWaitingDriver) {
    this.elementWaitingDriver = elementWaitingDriver;
  }

  @Override
  public boolean isVisible() {
    return executeAndSuppressException(elementWaitingDriver::isVisible);
  }

  @Override
  public boolean isVisible(long timeoutInSeconds) {
    return executeAndSuppressException(() -> elementWaitingDriver.isVisible(timeoutInSeconds));
  }

  @Override
  public boolean isVisible(long timeout, TemporalUnit unit) {
    return executeAndSuppressException(() -> elementWaitingDriver.isVisible(timeout, unit));
  }

  @Override
  public boolean isNotVisible() {
    return executeAndSuppressException(elementWaitingDriver::isNotVisible);
  }

  @Override
  public boolean isNotVisible(long timeoutInSeconds) {
    return executeAndSuppressException(() -> elementWaitingDriver.isNotVisible(timeoutInSeconds));
  }

  @Override
  public boolean isNotVisible(long timeout, TemporalUnit unit) {
    return executeAndSuppressException(() -> elementWaitingDriver.isNotVisible(timeout, unit));
  }

  @Override
  public boolean isClickable() {
    return executeAndSuppressException(elementWaitingDriver::isClickable);
  }

  @Override
  public boolean isClickable(long timeoutInSeconds) {
    return executeAndSuppressException(() -> elementWaitingDriver.isClickable(timeoutInSeconds));
  }

  @Override
  public boolean isClickable(long timeout, TemporalUnit unit) {
    return executeAndSuppressException(() -> elementWaitingDriver.isClickable(timeout, unit));
  }

  @Override
  public boolean attributeToBeNotEmpty(String attribute) {
    return executeAndSuppressException(() -> elementWaitingDriver.attributeToBeNotEmpty(attribute));
  }

  @Override
  public boolean attributeToBeNotEmpty(String attribute, long timeoutInSeconds) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeToBeNotEmpty(attribute, timeoutInSeconds));
  }

  @Override
  public boolean attributeToBeNotEmpty(String attribute, long timeout, TemporalUnit unit) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeToBeNotEmpty(attribute, timeout, unit));
  }

  @Override
  public boolean attributeToBe(String attribute, String value) {
    return executeAndSuppressException(() -> elementWaitingDriver.attributeToBe(attribute, value));
  }

  @Override
  public boolean attributeToBe(String attribute, String value, long timeoutInSeconds) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeToBe(attribute, value, timeoutInSeconds));
  }

  @Override
  public boolean attributeToBe(String attribute, String value, long timeout, TemporalUnit unit) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeToBe(attribute, value, timeout, unit));
  }

  @Override
  public boolean attributeContains(String attribute, String value) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeContains(attribute, value));
  }

  @Override
  public boolean attributeContains(String attribute, String value, long timeoutInSeconds) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeContains(attribute, value, timeoutInSeconds));
  }

  @Override
  public boolean attributeContains(String attribute, String value, long timeout,
      TemporalUnit unit) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.attributeContains(attribute, value, timeout, unit));
  }

  private boolean executeAndSuppressException(Runnable runnable) {
    try {
      runnable.run();
      return true;
    } catch (SessionLostException ex) {
      throw ex;
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public boolean exists() {
    return executeAndSuppressException(elementWaitingDriver::exists);
  }

  @Override
  public boolean exists(long l) {
    return executeAndSuppressException(() -> elementWaitingDriver.exists(l));
  }

  @Override
  public boolean exists(long l, TemporalUnit temporalUnit) {
    return executeAndSuppressException(() -> elementWaitingDriver.exists(l, temporalUnit));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function) {
    return executeAndSuppressException(() -> elementWaitingDriver.condition(function));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function, String message) {
    return executeAndSuppressException(() -> elementWaitingDriver.condition(function, message));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function, long timeoutInSeconds) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.condition(function, timeoutInSeconds));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function, long timeoutInSeconds,
      String message) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.condition(function, timeoutInSeconds, message));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function, long timeout,
      TemporalUnit unit) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.condition(function, timeout, unit));
  }

  @Override
  public <T> boolean condition(Function<IInteractiveElement, T> function, long timeout,
      TemporalUnit unit, String message) {
    return executeAndSuppressException(
        () -> elementWaitingDriver.condition(function, timeout, unit, message));
  }
}
