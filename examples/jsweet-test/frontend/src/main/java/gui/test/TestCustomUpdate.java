/* (c) https://github.com/MontiCore/monticore */
package gui.test;

import umlp.guiutil.GUIUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Component
public class TestCustomUpdate extends TestCustomUpdateTOP {

  private List<String> myField = Arrays.asList("First", "Second");

  @Override
  public String superComplexComputationDependentOnField() {
    return String.join(", ", myField);
  }

  @Override
  public Consumer<Void> changeFieldWithoutUpdate() {
    return Void -> {
      this.myField.add("Third");
    };
  }

  @Override
  public Consumer<Void> changeFieldWithUpdate() {
    return Void -> {
      this.myField.add("Fourth");
      GUIUtils.update(() -> this.getGemText0Value());
    };
  }
}
