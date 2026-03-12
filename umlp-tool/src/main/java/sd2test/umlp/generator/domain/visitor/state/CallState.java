package sd2test.umlp.generator.domain.visitor.state;

import sd2test.umlp.generator.domain.visitor.model.ActivityBar;
import sd2test.umlp.generator.domain.visitor.model.InteractionExtractable;

/**
 * A state describing a call, ergo something which might have an activity bar attached
 */
public interface CallState extends OCLAttachableState, InteractionExtractable {
  void setActivityBar(ActivityBar activityBar);
  VisitorState previousState();
}
