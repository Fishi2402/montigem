package e2e;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import domain.test_infrastructure.AbstractDomainE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static umlp.backendrte.test_infrastructure.playwright.ComponentKind.GemText;

public class ExampleCssTest extends AbstractDomainE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/example_basic/CssStyleTestExample"); //go to page we want to test
  }

  @Disabled
  @Test
  void testGemTextComponentKindLocator(Page page) {
    ElementHandle element = locator(page, ".testText", GemText).elementHandle();
    System.out.printf("Element: %s%n", element);
  }

  @Test
  void testStyles(Page page) {
    ElementHandle element = locator(page, ".testText", GemText).elementHandle();
    System.out.printf("Element: %s%n", element);

    String textAlign = CssUtil.getTextAlign(page, element);
    System.out.printf("Text align: %s%n", textAlign);

    String textColor = CssUtil.getTextColor(page, element);
    System.out.printf("Text color: %s%n", textColor);

    String fontFamily = CssUtil.getFontFamily(page, element);
    System.out.printf("Font family: %s%n", fontFamily);

    String fontSize = CssUtil.getFontSize(page, element);
    System.out.printf("Font size: %s%n", fontSize);

    String fontStyle = CssUtil.getFontStyle(page, element);
    System.out.printf("Font style: %s%n", fontStyle);

    String fontWeight = CssUtil.getFontWeight(page, element);
    System.out.printf("Font weight: %s%n", fontWeight);

    String lineHeight = CssUtil.getLineHeight(page, element);
    System.out.printf("Line height: %s%n", lineHeight);

    String background = CssUtil.getBackground(page, element);
    System.out.printf("Background: %s%n", background);

    String borderColor = CssUtil.getBorderColor(page, element);
    System.out.printf("Border color: %s%n", borderColor);

    String borderRadius = CssUtil.getBorderRadius(page, element);
    System.out.printf("Border radius: %s%n", borderRadius);

    String boxShadow = CssUtil.getBoxShadow(page, element);
    System.out.printf("Box shadow: %s%n", boxShadow);

    Object dimensions = CssUtil.getDimensions(page, element);
    System.out.printf("Dimensions: %s%n", dimensions);

    String display = CssUtil.getDisplay(page, element);
    System.out.printf("Display: %s%n", display);

    Object margin = CssUtil.getMargin(page, element);
    System.out.printf("Margin: %s%n", margin);

    String opacity = CssUtil.getOpacity(page, element);
    System.out.printf("Opacity: %s%n", opacity);

    Object padding = CssUtil.getPadding(page, element);
    System.out.printf("Padding: %s%n", padding);

    String position = CssUtil.getPosition(page, element);
    System.out.printf("Position: %s%n", position);

    String zIndex = CssUtil.getZIndex(page, element);
    System.out.printf("Z index: %s%n", zIndex);
  }
}
