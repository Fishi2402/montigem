package umlp.generator.gui.gentypescript.commands;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.generator.domain.decorator.commands.CalculateDecorator;

import java.util.Set;

public class TSCalculateDecorator extends CalculateDecorator {

  public TSCalculateDecorator(GlobalExtensionManagement glex, Set<String> supportedDeSers) {
    super(glex, supportedDeSers);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.commands.get.Constructor";
  }

  @Override
  protected String getGetEffectedGemIdsTemplate() {
    return "umlp.gui2ts.commands.GetEffectedGemIds";
  }

  @Override
  protected String getDoRunSimpleTemplate() {
    return "umlp.gui2ts.commands.calculate.DoRunSimple";
  }

  @Override
  protected String getDoRunListTemplate() {
    return "umlp.gui2ts.commands.calculate.DoRunList";
  }

  @Override
  protected String getGet_BuildJsonTemplate() {
    return "umlp.gui2ts.commands.deser.Get_BuildJson";
  }

  @Override
  protected String getGet_FromJsonObjectTemplate() {
    return "umlp.gui2ts.commands.deser.Get_FromJsonObject";
  }

}
