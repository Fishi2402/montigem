package gui.test;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;


@Component
public class Test1 extends Test1TOP {

  @Override
  public Consumer<Void> addProject() {
    return $ -> {
      DtcockpitManager.projectBuilder()
          .name("Test1")
          .git("Test2")
          .build();
    };
  }
}
