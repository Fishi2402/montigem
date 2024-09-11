package e2e;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CssUtil {
  protected static final String textAlignScript = loadScript("computed_css/text/align.js");
  protected static final String textColorScript = loadScript("computed_css/text/color.js");
  protected static final String fontFamilyScript = loadScript("computed_css/text/fontFamily.js");
  protected static final String fontSizeScript = loadScript("computed_css/text/fontSize.js");
  protected static final String fontStyleScript = loadScript("computed_css/text/fontStyle.js");
  protected static final String fontWeightScript = loadScript("computed_css/text/fontWeight.js");
  protected static final String lineHeightScript = loadScript("computed_css/text/lineHeight.js");
  protected static final String backgroundScript = loadScript("computed_css/background.js");
  protected static final String borderColorScript = loadScript("computed_css/borderColor.js");
  protected static final String borderRadiusScript = loadScript("computed_css/borderRadius.js");
  protected static final String boxShadowScript = loadScript("computed_css/boxShadow.js");
  protected static final String dimensionsScript = loadScript("computed_css/dimensions.js");
  protected static final String displayScript = loadScript("computed_css/display.js");
  protected static final String marginScript = loadScript("computed_css/margin.js");
  protected static final String opacityScript = loadScript("computed_css/opacity.js");
  protected static final String paddingScript = loadScript("computed_css/padding.js");
  protected static final String positionScript = loadScript("computed_css/position.js");
  protected static final String zIndexScript = loadScript("computed_css/zIndex.js");

  public static String getTextAlign(Page page, ElementHandle elementHandle) {
    return page.evaluate(textAlignScript, elementHandle).toString();
  }

  public static String getTextColor(Page page, ElementHandle elementHandle) {
    return page.evaluate(textColorScript, elementHandle).toString();
  }

  public static String getFontFamily(Page page, ElementHandle elementHandle) {
    return page.evaluate(fontFamilyScript, elementHandle).toString();
  }

  public static String getFontSize(Page page, ElementHandle elementHandle) {
    return page.evaluate(fontSizeScript, elementHandle).toString();
  }

  public static String getFontStyle(Page page, ElementHandle elementHandle) {
    return page.evaluate(fontStyleScript, elementHandle).toString();
  }

  public static String getFontWeight(Page page, ElementHandle elementHandle) {
    return page.evaluate(fontWeightScript, elementHandle).toString();
  }

  public static String getLineHeight(Page page, ElementHandle elementHandle) {
    return page.evaluate(lineHeightScript, elementHandle).toString();
  }

  public static String getBackground(Page page, ElementHandle elementHandle) {
    return page.evaluate(backgroundScript, elementHandle).toString();
  }

  public static String getBorderColor(Page page, ElementHandle elementHandle) {
    return page.evaluate(borderColorScript, elementHandle).toString();
  }

  public static String getBorderRadius(Page page, ElementHandle elementHandle) {
    return page.evaluate(borderRadiusScript, elementHandle).toString();
  }

  public static String getBoxShadow(Page page, ElementHandle elementHandle) {
    return page.evaluate(boxShadowScript, elementHandle).toString();
  }

  public static Object getDimensions(Page page, ElementHandle elementHandle) {
    return page.evaluate(dimensionsScript, elementHandle);
  }

  public static String getDisplay(Page page, ElementHandle elementHandle) {
    return page.evaluate(displayScript, elementHandle).toString();
  }

  public static Object getMargin(Page page, ElementHandle elementHandle) {
    return page.evaluate(marginScript, elementHandle);
  }

  public static String getOpacity(Page page, ElementHandle elementHandle) {
    return page.evaluate(opacityScript, elementHandle).toString();
  }

  public static Object getPadding(Page page, ElementHandle elementHandle) {
    return page.evaluate(paddingScript, elementHandle);
  }

  public static String getPosition(Page page, ElementHandle elementHandle) {
    return page.evaluate(positionScript, elementHandle).toString();
  }

  public static String getZIndex(Page page, ElementHandle elementHandle) {
    return page.evaluate(zIndexScript, elementHandle).toString();
  }

  protected static String loadScript(String path) {
    try {
      return IOUtils.toString(
          Objects.requireNonNull(CssUtil.class.getResource("/e2e_eval/" + path)),
          StandardCharsets.UTF_8
      );
    } catch (IOException e) {
      throw new RuntimeException("Can not load script " + path, e);
    }
  }
}
