package test_gui;

import loadingtests.LeftSide;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InnerWithHWC extends InnerWithHWCTOP {
  @Override
  public List<LeftSide> getIt1Iterable() {
    // works: return super.getIt1Iterable();
    List<LeftSide> sides = new ArrayList<>();
    // Let's assume we sort/filter the list somehow (but we don't do stay with a MVP)
    for (LeftSide l : super.getIt1Iterable())
      sides.add(l);
    return sides;
  }
}
