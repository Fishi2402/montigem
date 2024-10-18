package umlp.test_rte.playwright;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

public interface PlaywrightUtils {
  default void waitForAngular(Page page) {
    //@formatter:off
    page.evaluate("async () => {" +
                  "  if (window.getAllAngularTestabilities) {\n" +
                  "    // @ts-expect-error\n" +
                  "    await Promise.all(window.getAllAngularTestabilities().map(whenStable));\n" +
                  "    \n" +
                  "    // @ts-expect-error\n" +
                  "    async function whenStable(testability) {\n" +
                  "      return new Promise(res => testability.whenStable(res));\n" +
                  "    }\n" +
                  "  }\n" +
                  "}");
    //@formatter:on
  }

  /**
   * Waits until the web socket of the application has connected.
   * <p>
   * <strong>ATTENTION: </strong>
   * <p>Calling this method requires the {@code app.component.ts} to
   * have the following line of code at the end of the constructor:
   * <pre>{@code
   * (<any>window).websocket_connected = true;
   * }</pre>
   * Otherwise, this method can potentially never return!
   *
   * @param page The current {@link Page}
   */
  default void waitForWebsocket(Page page) {
    long start = System.currentTimeMillis();

    Locator.EvaluateOptions options = new Locator.EvaluateOptions();
    options.setTimeout(30_000);
    page.locator("body").evaluate(
        //@formatter:off
        "new Promise(resolve => {\n" +
        "  const checkReady = () => {\n" +
        "    if (window.websocket_connected != undefined) {\n" +
        "      resolve(window.websocket_connected);\n" +
        "    } else {\n" +
        "      setTimeout(checkReady, 100);\n" +
        "    }\n" +
        "  };\n" +
        "  checkReady();\n" +
        "});"
        //@formatter:on
        , null, options);

    System.out.println("Waiting for websocket took " + (System.currentTimeMillis() - start) + "ms");
  }

  default boolean waitInit(Page page, String pageName) {
    String pageComponentName = pageName.endsWith("Component") ? pageName : pageName + "Component";
    //@formatter:off
    String command =
        "async () => { \n" +
            "    const startTime = new Date().getTime(); \n" +
            "    while (true) { \n" +
            "        if (window.allVarsInited2 && window.allVarsInited2[\"" + pageComponentName + "\"]) { \n" +
            "            console.log(\"all variables are initialized\"); \n" +
            "            return true; \n" +
            "        } \n" +
            "        if (new Date().getTime() - startTime > 10000) { \n" +
            "            console.log('Timed out waiting for allVarsInited'); \n"
            + "          return false; \n" +
            "        } \n" +
            "        await new Promise(resolve => setTimeout(resolve, 100)); \n" +
            "    } \n" +
            "} \n";
    return Boolean.parseBoolean(page.evaluate(command).toString());
    //@formatter:on
  }

  /**
   * Waits until the page is fully loaded. Has the same requirements as {@link #waitForWebsocket(Page)}
   *
   * @param page The {@link Page}
   */
  default void waitRdy(Page page) {
    waitForAngular(page);
    waitForWebsocket(page);
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  /**
   * Waits for a {@link Dialog} to occur on the provided {@link Page} while the trigger is run
   *
   * @param page    {@link Page} where the {@link Dialog} should occur
   * @param trigger {@link Runnable} which should trigger the {@link Dialog}
   * @return A {@link AwaitDialog} instance
   */
  default AwaitDialog awaitDialog(Page page, Runnable trigger) {
    return new AwaitDialog(page, trigger);
  }

  String getUrlBase();

  default String getSaveUrlBase() {
    return getUrlBase().endsWith("/") ? getUrlBase() : getUrlBase() + "/";
  }

  /**
   * Navigates to a new route and waits for the website to reload.
   * <p>
   * Has the same requirements as {@link #waitRdy(Page)}!
   * <p>
   * <strong>Example</strong>
   * <pre>{@code
   * navAndWait(page, "/person/Overview");
   * }</pre>
   *
   * @param page  The {@link Page}
   * @param route Where to navigate
   */
  default void navAndWait(Page page, String route) {
    page.navigate(getSaveUrlBase() + route.replaceAll("^/*", ""));
    waitRdy(page);
  }

  /**
   * Gets the first {@link Locator} that matches the given selector from a {@link Page} and from that gets the
   * underlying html element of the respective GuiDSL component.
   *
   * <p>Should be preferred over statements like {@code page.locator(".foo > button")} since the locator of the
   * underlying html element may change
   *
   * <p><strong>Example</strong>
   * <pre>{@code
   * locator(page, ".foo", ComponentKind.GemButton).click();
   * }</pre>
   *
   * @param page     The {@link Page} to get the {@link Locator} from
   * @param selector Selector for the {@link Locator} to get
   * @param kind     What kind of component the selector denounces
   * @return The respective {@link Locator}
   */
  default Locator locator(Page page, String selector, ComponentKindLike kind) {
    return locator(new PageAdapter(page), selector, kind);
  }

  /**
   * Gets the first {@link Locator} that matches the given selector inside another {@link Locator} and from that gets the
   * underlying html element of the respective GuiDSL component.
   *
   * <p>Should be preferred over statements like {@code locator.locator(".foo > button")} since the locator of the
   * underlying html element may change
   *
   * <p><strong>Example</strong>
   * <pre>{@code
   * locator(locator, ".foo", ComponentKind.GemButton).click();
   * }</pre>
   *
   * @param locator  The {@link Locator} to get the next {@link Locator} from
   * @param selector Selector for the {@link Locator} to get
   * @param kind     What kind of component the selector denounces
   * @return The respective {@link Locator}
   */
  default Locator locator(Locator locator, String selector, ComponentKindLike kind) {
    return locator(new LocatorAdapter(locator), selector, kind);
  }

  default Locator locator(LocatorHolder locatorHolder, String selector, ComponentKindLike kind) {
    return locatorHolder.locator(selector).first().locator(kind.selector());
  }

  //Page and Locator each define the locator function themselves
  public interface LocatorHolder {
    Locator locator(String selector);
  }

  public static class PageAdapter implements LocatorHolder {

    private final Page page;

    public PageAdapter(Page page) {
      this.page = page;
    }

    @Override
    public Locator locator(String selector) {
      return page.locator(selector);
    }
  }

  public static class LocatorAdapter implements LocatorHolder {
    private final Locator locator;

    public LocatorAdapter(Locator locator) {
      this.locator = locator;
    }

    @Override
    public Locator locator(String selector) {
      return locator.locator(selector);
    }
  }
}
