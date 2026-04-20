package gui.endeffector;

import java.util.function.Consumer;

public class EndEffectorCreate extends EndEffectorCreateTOP {

  public Consumer<Void> createEndEffector() {
    return $ -> {
      this.getEndEffector().build();
    };
  }
}
