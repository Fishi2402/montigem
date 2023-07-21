package umlp.generator;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateController;

import java.util.List;
import java.util.Map;

class OriginHookPoint extends HookPoint {
  private final Map<ASTNode, String> origins;

  OriginHookPoint(Map<ASTNode, String> origins) {
    this.origins = origins;
  }

  @Override
  public String processValue(TemplateController controller, ASTNode ast) {
    return origins.getOrDefault(ast, "");
  }

  @Override
  public String processValue(TemplateController controller, List<Object> args) {
    return "";
  }

  @Override
  public String processValue(TemplateController controller, ASTNode node, List<Object> args) {
    return origins.getOrDefault(node, "");
  }
}
