package umlp.backendrte.test_infrastructure.playwright;

import com.microsoft.playwright.Page;

/**
 * Describes common component kinds within the umlp language and provides their selectors for the underlying html
 * elements.
 *
 * <p>Primarily intended for usage in the {@link AbstractPlaywrightE2ETest#locator(Page, String, ComponentKindLike)} method.
 */
public enum ComponentKind implements ComponentKindLike {
  /**
   * <strong>Example</strong>
   * <pre>{@code
   * locator(page, ".createExhibit", GemButton).click();
   * }</pre>
   */
  GemButton("button"),
  /**
   * <strong>Example</strong>
   * <pre>{@code
   * locator(user, ".active", GemCheckbox).check();
   * }</pre>
   */
  GemCheckbox("input"),
  /**
   * Can be used for normal text input and date input. Date has to be in format
   * {@code yyyy-MM-dd}
   * <p>
   * <strong>Example</strong>
   * <pre>{@code
   * locator(page, ".firstnameInput", GemTextInput).fill("Charlie");
   * }</pre>
   * <strong>Date example</strong>
   * <pre>{@code
   * //set the 07.05.2024
   * locator(page, ".birthday", GemTextInput).fill("2024-05-07");
   * }</pre>
   */
  GemTextInput("input"),
  /**
   * <strong>Example</strong>
   * <pre>{@code
   * locator(page, ".departmentInput", GemTextDropdownInput).selectOption("Biologie");
   * }</pre>
   */
  GemTextDropdownInput("select"),
  /**
   * <strong>Example</strong>
   * <pre>{@code
   * locator(page, ".titleInput", GemTextAreaInput).fill("Alliterationen in Namen");
   * }</pre>
   */
  GemTextAreaInput("textarea"),
  /**
   * <strong>Example</strong>
   * <pre>{@code
   * locator(page, ".actualThesisUpload", PDFDragNDrop).setInputFiles(Paths.get("src/test/resources/ToDelete.pdf"));
   * }</pre>
   */
  PDFDragNDrop("input[type='file']"),
  ;

  private final String selector;

  ComponentKind(String selector) {
    this.selector = selector;
  }

  @Override
  public String selector() {
    return selector;
  }
}
