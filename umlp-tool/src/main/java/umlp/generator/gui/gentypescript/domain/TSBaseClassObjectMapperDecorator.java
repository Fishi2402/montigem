/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.Joiners;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSBaseClassObjectMapperDecorator extends BaseClassObjectMapperDecorator {

  public TSBaseClassObjectMapperDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected String getConstructorTemplate() {
    return "umlp.gui2ts.baseclassobjectmapper.TSConstructor";
  }

  @Override
  protected String getInitTemplate() {
    return "umlp.gui2ts.baseclassobjectmapper.TSInit";
  }

}
