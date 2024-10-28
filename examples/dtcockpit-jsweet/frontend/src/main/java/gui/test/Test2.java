package gui.test;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;


@Component
public class Test2 extends Test2TOP {

  @Override
  public Consumer<Void> addProject() {
    return $ -> {
      DtcockpitManager.projectBuilder()
          .name("Test2")
          .git("Test2")
          .build();
    };
  }
}
