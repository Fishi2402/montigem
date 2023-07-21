/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.domain.decorator.BaseClassObjectMapperDecorator;
import umlp.generator.domain.decorator.BuilderDecorator;
import umlp.generator.domain.decorator.CommandObjectMapperDecorator;
import umlp.generator.domain.decorator.DeSerBuilderDecorator;
import umlp.generator.domain.decorator.DiagramObserverDecorator;
import umlp.generator.domain.decorator.ObserverDecorator;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class TSDiagramObserverDecorator extends DiagramObserverDecorator {

  protected String astcdPackage;

  protected ASTCDCompilationUnit origCD;

  public TSDiagramObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    this.astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE).getName();
    this.origCD = origCD;
    super.transform(origCD, decoratedCD);
  }

  protected ASTCDInterface createInterface(ASTCDCompilationUnit origCD) {
    ASTCDInterface clazz = super.createInterface(origCD);

    for (ASTCDClass c : this.origCD.getCDDefinition().getCDClassesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "." + c.getName());
    }
    for (ASTCDEnum c : this.origCD.getCDDefinition().getCDEnumsList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
          + c.getName());
    }
    for (ASTCDInterface c : this.origCD.getCDDefinition().getCDInterfacesList()) {
      getCD4C().addImport(clazz, this.astcdPackage + "."
          + c.getName());
    }

    return clazz;
  }
}
