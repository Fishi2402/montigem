package umlp.generator.domain.visitor.state;

import umlp.generator.domain.visitor.model.ActivityBar;
import umlp.generator.domain.visitor.model.InteractionExtractable;

/**
 * A state describing a call, ergo something which might have an activity bar attached
 */
public interface CallState extends OCLAttachableState, InteractionExtractable {
  void setActivityBar(ActivityBar activityBar);
  VisitorState previousState();
}
