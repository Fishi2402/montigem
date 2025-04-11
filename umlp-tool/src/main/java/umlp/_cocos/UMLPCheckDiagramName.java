/* (c) https://github.com/MontiCore/monticore */
package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;

public class UMLPCheckDiagramName implements CDBasisASTCDDefinitionCoCo, CoCoTestable {

  public static final String ERROR_CODE = "0x70873";

  public static final String ERROR_MSG_FORMAT = " The type %s must not have the same name as the diagram.";

  @Override
  public void check(ASTCDDefinition node) {
    String diagramName = node.getName();
    node.getCDClassesList().stream()
        .filter(c -> diagramName.equals(c.getName()))
        .findAny().ifPresent(c -> logError(c, diagramName));
    node.getCDInterfacesList().stream()
        .filter(c -> diagramName.equals(c.getName()))
        .findAny().ifPresent(c -> logError(c, diagramName));
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }

  @Override
  public String getErrorMessageFormat() {
    return ERROR_MSG_FORMAT;
  }
}
