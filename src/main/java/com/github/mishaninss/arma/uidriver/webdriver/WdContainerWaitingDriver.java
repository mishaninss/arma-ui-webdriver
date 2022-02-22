package com.github.mishaninss.arma.uidriver.webdriver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.html.interfaces.IElementsContainer;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IContainerQuietWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IContainerWaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdContainerWaitingDriver implements IContainerWaitingDriver {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebDriverProperties properties;

    private final IElementsContainer container;

    @WaitingDriver
    private IWaitingDriver waitingDriver;

    public WdContainerWaitingDriver(IElementsContainer container) {
        this.container = container;
    }

    @Override
    public IContainerQuietWaitingDriver quietly() {
        return applicationContext.getBean(IContainerQuietWaitingDriver.class, this);
    }

    @Override
    public void isVisible() {
        isVisible(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void isVisible(long timeoutInSeconds) {
        isVisible(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void isVisible(long timeout, TemporalUnit unit) {
        if (StringUtils.isNotBlank(container.getLocator())) {
            waitingDriver.waitForElementIsVisible(container, timeout, unit);
        } else {
            allElementsAreVisible(timeout, unit);
        }
    }

    @Override
    public void isNotVisible() {
        isNotVisible(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void isNotVisible(long timeoutInSeconds) {
        isNotVisible(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void isNotVisible(long timeout, TemporalUnit unit) {
        if (StringUtils.isNotBlank(container.getLocator())) {
            waitingDriver.waitForElementIsNotVisible(container, timeout, unit);
        } else {
            container.getElements().values().stream()
                    .filter(element -> !element.isOptional())
                    .forEach(element -> waitingDriver.waitForElementIsNotVisible(element, timeout, unit));

        }
    }

    @Override
    public void allElementsAreVisible() {
        allElementsAreVisible(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void allElementsAreVisible(long timeoutInSeconds) {
        allElementsAreVisible(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreVisible(long timeout, TemporalUnit unit) {
        container.getElements().values().stream()
                .filter(element -> !element.isOptional())
                .forEach(element -> waitingDriver.waitForElementIsVisible(element, timeout, unit));
    }

    @Override
    public void allElementsAreClickable() {
        allElementsAreClickable(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void allElementsAreClickable(long timeoutInSeconds) {
        allElementsAreClickable(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void allElementsAreClickable(long timeout, TemporalUnit unit) {
        container.getElements().values().stream()
                .filter(element -> !element.isOptional())
                .forEach(element -> waitingDriver.waitForElementIsClickable(element, timeout, unit));
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition) {
        return waitingDriver.waitForCondition(() -> condition.apply(container));
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), message);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeoutInSeconds, message);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeoutInSeconds) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeoutInSeconds);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeout, unit);
    }

    @Override
    public <T> T condition(Function<IElementsContainer, T> condition, long timeout, TemporalUnit unit, String message) {
        return waitingDriver.waitForCondition(() -> condition.apply(container), timeout, unit, message);
    }
}
