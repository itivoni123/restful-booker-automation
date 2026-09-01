package com.restfulbooker.automation.ui.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.ui.support.ScreenshotOnFailureExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @RegisterExtension
    final ScreenshotOnFailureExtension screenshotOnFailure =
            new ScreenshotOnFailureExtension(() -> page);

    @BeforeEach
    void setUp() {

        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(ConfigManager.isHeadless())
        );

        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {

        if (context != null) {
            context.close();
        }

        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}