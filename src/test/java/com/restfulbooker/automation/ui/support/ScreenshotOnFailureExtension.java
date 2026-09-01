package com.restfulbooker.automation.ui.support;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class ScreenshotOnFailureExtension
        implements AfterTestExecutionCallback {

    private final Supplier<Page> pageSupplier;

    public ScreenshotOnFailureExtension(Supplier<Page> pageSupplier) {
        this.pageSupplier = pageSupplier;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {

        if (context.getExecutionException().isEmpty()) {
            return;
        }

        Page page = pageSupplier.get();

        if (page == null || page.isClosed()) {
            return;
        }

        String testName = context.getRequiredTestMethod().getName();

        Path screenshotPath = Paths.get(
                "target",
                "screenshots",
                testName + ".png"
        );

        page.screenshot(
                new Page.ScreenshotOptions()
                        .setPath(screenshotPath)
                        .setFullPage(true)
        );
    }
}