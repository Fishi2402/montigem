package umlp.test_rte.playwright;

/**
 * Describes objects, which can be selected as locators, but which require further selection for underlying html
 * components to be actually of use
 *
 * @see ComponentKind
 */
public interface ComponentKindLike {

  /**
   * Gets the selector to the underlying html component
   *
   * @return The selector to the underlying html component
   */
  String selector();
}
