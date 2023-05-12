/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.io.paths.MCPath;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.DomainClassDecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.util.PathUtil;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSDeSerBuilderDecorator extends DeSerBuilderDecorator {

  public TSDeSerBuilderDecorator(final GlobalExtensionManagement glex, MCPath hwPath) {
    super(glex, hwPath);
  }

  protected String getConstructorTemplate() {
    return "umlp.gui2ts.deser_builder.TSConstructor";
  }

  protected String getsetSimpleTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetSimple";
  }

  protected String getsetGemTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetGem";
  }

  protected String getsetGemListIdsTemplate() {
    return "umlp.gui2ts.deser_builder.TSsetGemListIds";
  }

}
