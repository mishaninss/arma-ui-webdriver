package com.github.mishaninss.arma.aspects;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.exceptions.InteractionException;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.html.interfaces.IInteractiveElement;
import com.github.mishaninss.arma.html.interfaces.INamed;

import java.lang.reflect.InvocationTargetException;

@Component
public class WebdriverInteractiveElementExceptionBuilder extends DefaultIInteractiveExceptionBuilderImpl {
    @Override
    public RuntimeException buildException(IInteractiveElement element, String action, Exception ex) {
        Throwable cause = ex;
        if (cause instanceof InvocationTargetException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        StringBuilder sb = new StringBuilder();
        if (cause instanceof NoSuchElementException) {
            sb.append("Элемент ").append(((INamed) element).getLoggableName()).append(" не найден");
        } else {
            sb.append("Ошибка при попытке ").append(action);
            String name = "";
            if (element instanceof INamed) {
                name = ((INamed) element).getName();
            }

            if (!StringUtils.isBlank(name)) {
                sb.append(" [").append(name).append("]");
            } else {
                sb.append(" [").append(element.getLocator()).append("]");
            }
        }

        sb.append("\nЛокатор: ").append(element.getLocatorsPath());

        if (cause instanceof SessionLostException) {
            return clearStacktrace(new SessionLostException(sb.toString(), cause));
        } else {
            if (browserDriver.isBrowserStarted()) {
                reporter.attachScreenshot(pageDriver.takeScreenshot());
                if (properties.driver().areConsoleLogsEnabled()) {
                    reporter.attachText(StringUtils.join(browserDriver.getLogEntries("browser"), "\n"), "Browser logs");
                }
                sb.append("\nURL: ").append(pageDriver.getCurrentUrl());
                sb.append("\nЗаголовок страницы: ").append(pageDriver.getPageTitle());
            }

            return clearStacktrace(new InteractionException(sb.toString(), cause));
        }
    }
}
