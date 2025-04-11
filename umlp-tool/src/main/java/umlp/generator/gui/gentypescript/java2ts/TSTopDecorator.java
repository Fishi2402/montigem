/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.io.paths.MCPath;
import umlp.generator.gui.GUIGeneratorUtil;

@SuppressWarnings("unused") // used in ftl
public class TSTopDecorator extends TopDecorator {

  public TSTopDecorator(MCPath hwPath) {
    super(hwPath);
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit) {
    return this.decorate(compUnit, false);
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit, boolean importHW) {

    // Ensure determineQualifiedName works by filling the nameMap
    CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
    DetermineNameVisitor nameVisitor = new DetermineNameVisitor();
    traverser.add4CDBasis(nameVisitor);
    traverser.add4CDInterfaceAndEnum(nameVisitor);
    compUnit.accept(traverser);

    for (ASTCDClass cdClass : compUnit.getCDDefinition().getCDClassesList()) {
      String qualifiedName = determineQualifiedName(cdClass, compUnit);
      boolean existsHw = GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, qualifiedName);
      if (!existsHw) {
        checkNeedsHandwrittenClass(cdClass, qualifiedName);
      }
      if (existsHw && !GUIGeneratorUtil.getInstance().isComponent(cdClass)) {
        if (importHW && !GUIGeneratorUtil.getInstance().isCommand(cdClass)) {
          CD4C.getInstance().addImport(cdClass, nameMap.get(cdClass) + "." + cdClass.getName());
        }
        applyTopMechanism(cdClass);
      }
    }

    for (ASTCDInterface cdInterface : compUnit.getCDDefinition().getCDInterfacesList()) {
      if (GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, determineQualifiedName(cdInterface, compUnit))) {
        if (importHW) {
          CD4C.getInstance().addImport(cdInterface, GUIGeneratorUtil.getInstance().getPackageName(compUnit) + "." + cdInterface.getName());
        }
        applyTopMechanism(cdInterface);
      }
    }

    for (ASTCDEnum cdEnum : compUnit.getCDDefinition().getCDEnumsList()) {
      if (GUIGeneratorUtil.getInstance().existsHandwrittenClass(hwPath, determineQualifiedName(cdEnum, compUnit))) {
        if (importHW) {
          CD4C.getInstance().addImport(cdEnum, GUIGeneratorUtil.getInstance().getPackageName(compUnit) + "." + cdEnum.getName());
        }
        applyTopMechanism(cdEnum);
      }
    }

    // Run the default top decorator afterwards
    super.decorate(compUnit);

    return compUnit;
  }

  protected void checkNeedsHandwrittenClass(ASTCDClass cdClass, String qualifiedName) {
    super.checkNeedsHandwrittenClass(false, cdClass, qualifiedName);
  }


  @Override
  protected void checkNeedsHandwrittenClass(boolean existsHw, ASTCDClass cdClass) {
  }

  @Override
  protected void checkNeedsHandwrittenClass(boolean existsHw, ASTCDClass cdClass, String qualifiedName) {
  }


}
