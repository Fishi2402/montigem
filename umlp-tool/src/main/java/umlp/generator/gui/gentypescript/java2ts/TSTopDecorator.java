/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import umlp.generator.gui.GUIGeneratorUtil;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TSTopDecorator extends TopDecorator {

  public TSTopDecorator(MCPath hwPath) {
    super(hwPath);
  }

  // TODO AGe: do not check yet
  @Override
  protected void checkNeedsHandwrittenClass(boolean existsHw, ASTCDClass cdClass) {
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit) {
    return this.decorate(compUnit, false);
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit, boolean importHW) {
    super.decorate(compUnit);
    for (ASTCDClass cdClass : compUnit.getCDDefinition().getCDClassesList()) {
      if (!GUIGeneratorUtil.getInstance().isComponent(cdClass) && GUIGeneratorUtil.getInstance().existsHWC(hwPath, determineQualifiedName(cdClass, compUnit))) {
        if (importHW) {
          CD4C.getInstance().addImport(cdClass, GUIGeneratorUtil.getInstance().getPackageName(compUnit) + "." + cdClass.getName());
        }
        applyTopMechanism(cdClass);
      }
    }

    for (ASTCDInterface cdInterface : compUnit.getCDDefinition().getCDInterfacesList()) {
      if (GUIGeneratorUtil.getInstance().existsHWC(hwPath, determineQualifiedName(cdInterface, compUnit))) {
        if (importHW) {
          CD4C.getInstance().addImport(cdInterface, GUIGeneratorUtil.getInstance().getPackageName(compUnit) + "." + cdInterface.getName());
        }
        applyTopMechanism(cdInterface);
      }
    }

    for (ASTCDEnum cdEnum : compUnit.getCDDefinition().getCDEnumsList()) {
      if (GUIGeneratorUtil.getInstance().existsHWC(hwPath, determineQualifiedName(cdEnum, compUnit))) {
        if (importHW) {
          CD4C.getInstance().addImport(cdEnum, GUIGeneratorUtil.getInstance().getPackageName(compUnit) + "." + cdEnum.getName());
        }
        applyTopMechanism(cdEnum);
      }
    }

    return compUnit;
  }

  protected String getPackageName(ASTCDCompilationUnit origCD) {
    String origPackage = Names.constructQualifiedName(
        origCD.getCDPackageList(), origCD.getCDDefinition().getName()
    );
    return origPackage;
  }

  protected boolean existsHandwrittenClass(MCPath targetPath, String qualifiedName) {
    String hwFile = Names.getPathFromPackage(qualifiedName) + ".ts";
    Optional<URL> hwFilePath = targetPath.find(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Optional<Path> hwPath = MCPath.toPath(hwFilePath.get());
      hwPath.ifPresent(path -> Reporting.reportUseHandwrittenCodeFile(path,
          Paths.get(hwFile)));
    }
    Reporting.reportHWCExistenceCheck(targetPath, Paths.get(hwFile), hwFilePath);
    return result;
  }

}
