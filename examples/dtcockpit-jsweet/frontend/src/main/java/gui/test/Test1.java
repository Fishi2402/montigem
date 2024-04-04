package gui.test;

import dtcockpit.DtcockpitManager;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;


@Component
public class Test1 extends Test1TOP {
  public Test1(GemList projects) {
    super(projects);
  }

  @Override
  public void addProject(Object event) {
    DtcockpitManager.projectBuilder()
        .name("Test1")
        .git("Test2")
        .build();
  }
}
