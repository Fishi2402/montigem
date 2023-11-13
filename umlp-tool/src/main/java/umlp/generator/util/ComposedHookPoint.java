/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.util;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComposedHookPoint extends HookPoint {
  private final List<HookPoint> hookPoints;

  public ComposedHookPoint(HookPoint... hookPoints){
    this(Arrays.stream(hookPoints).collect(Collectors.toList()));
  }

  public ComposedHookPoint(List<HookPoint> hookPoints) {
    this.hookPoints = hookPoints;
  }

  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return hookPoints.stream().map(hp -> hp.processValue(controller, ast)).collect(Collectors.joining());
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return hookPoints.stream().map(hp -> hp.processValue(controller, args)).collect(Collectors.joining());
  }

  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    return hookPoints.stream().map(hp -> hp.processValue(controller, node, args)).collect(Collectors.joining());
  }
}
