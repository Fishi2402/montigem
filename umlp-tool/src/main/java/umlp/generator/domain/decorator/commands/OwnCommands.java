package umlp.generator.domain.decorator.commands;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;

import static de.monticore.types.check.SymTypePrimitive.box;

public interface OwnCommands {

  String COMMAND = "command";
  String REQUEST = "request";
  String STATUS_RESULT = "StatusResult";
  String SIMPLE_RESULT = "SimpleResult";
  String LIST_RESULT = "ListResult";

  default String buildReturnType(ASTCDMethod command, ASTCDClass ast) {
    ASTMCReturnType retType = command.getMCReturnType();
    if (retType.isPresentMCVoidType()) {
      CD4C.getInstance().addImport(ast, PathUtil.getBackendRTECommandPath(STATUS_RESULT));
      return STATUS_RESULT;
    }
    else if (retType.isPresentMCType()) {
      ASTMCType mcType = retType.getMCType();

      if (GenService.isList(mcType)) {
        return buildListResult(GenService.getInnerType(mcType), ast);
      }
      if (GenService.isOptional(mcType)) {
        return buildSimpleResult(GenService.getInnerType(mcType), ast);
      }
      return buildSimpleResult(UMLPMill.prettyPrint(mcType, false), ast);
    }
    return buildSimpleResult("Object", ast);
  }

  default String buildExtendedCommandType(String typeParam) {
    return new StringBuilder(StringTransformations.capitalize(REQUEST)).append("<")
               .append(box(typeParam)).append(">").toString();
  }

  default String buildSimpleResult(String typeParam, ASTCDClass ast) {
    CD4C.getInstance().addImport(ast, PathUtil.getBackendRTECommandPath(SIMPLE_RESULT));
    return new StringBuilder(SIMPLE_RESULT).append("<").append(box(typeParam)).append(">").toString();
  }

  default String buildListResult(String typeParam, ASTCDClass ast) {
    CD4C.getInstance().addImport(ast, PathUtil.getBackendRTECommandPath(LIST_RESULT));
    CD4C.getInstance().addImport(ast, "java.util.List");
    return new StringBuilder(LIST_RESULT).append("<").append(box(typeParam)).append(", List<")
               .append(box(typeParam)).append(">>").toString();
  }

  default boolean isGem(SymTypeExpression type) {
    return GenService.isCDType(type)
               || (GenService.isOptional(type) && GenService.isCDType(((SymTypeOfGenerics) type).getArgument(0)));
  }

  default boolean isGemList(SymTypeExpression type) {
    return GenService.isList(type) && GenService.isCDType(((SymTypeOfGenerics) type).getArgument(0));
  }

}
