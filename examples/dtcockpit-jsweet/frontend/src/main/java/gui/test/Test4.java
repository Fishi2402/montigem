package gui.test;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;


@Component
public class Test4 extends Test4TOP {
  public Test4(GemList projects) {
    super(projects);
  }



  @Override
  public String getPnameValue(Project p) {
    return "Hello world: " + p.getName();
  }
}
