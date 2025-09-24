package umlp.generator.domain.visitor.model.interaction;

import umlp.generator.domain.visitor.model.ocl.OCLStatement;

import java.util.List;

public interface Interaction {

  /**
   * Get whether this interaction is complete, ergo whether no parameters have been omitted or
   * marked as incomplete
   * @return true if this is complete
   */
  boolean isComplete();

  List<OCLStatement> oclStatements();

  <T> T matchVariant(InteractionVariantMatcher<T> matcher);
}
