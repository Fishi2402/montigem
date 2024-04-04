package gui.test;

import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;

import dtcockpit.Project;


@Component
public class Test3HWC extends Test3HWCTOP {
  public Test3HWC(GemList projects) {
    super(projects);
  }

  // TODO: https://git.rwth-aachen.de/monticore/monticore/-/issues/3971
  // Error: build/appgui/gui/test/Test3HWC.ts:41:9 - error TS7030: Not all code paths return a value.
//  @Override
//  public GemList<Project> getInnerProjects() {
////     TODO: Can we filter this one?
//    return super.getInnerProjects();
//  }

  @Override
  public int getInnerMaxValue() {
    return 42;
  }

  @Override
  public int getInnerMinValue() {
    return 42;
  }
}
