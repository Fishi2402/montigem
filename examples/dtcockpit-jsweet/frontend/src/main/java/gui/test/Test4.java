package gui.test;

import dtcockpit.Project;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;


@Component
public class Test4 extends Test4TOP {

  @Override
  public String getPnameValue(Project p) {
    return "Hello world: " + p.getName();
  }
}
