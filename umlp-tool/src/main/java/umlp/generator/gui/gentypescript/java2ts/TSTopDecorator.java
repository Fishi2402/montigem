/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.java2ts;

import de.monticore.cd.codegen.TopDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._visitor.CDInterfaceAndEnumVisitor2;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TSTopDecorator extends TopDecorator {

  public TSTopDecorator(MCPath hwPath) {
    super(hwPath);
  }

  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compUnit) {
    CD4CodeTraverser traverser = CD4CodeMill.traverser();
    DetermineNameVisitor nameVisitor = new DetermineNameVisitor();
    traverser.add4CDBasis(nameVisitor);
    traverser.add4CDInterfaceAndEnum(nameVisitor);
    compUnit.accept(traverser);

    compUnit.getCDDefinition().getCDClassesList().stream()
        .filter(
            cdClass -> existsHandwrittenClass(hwPath, determineQualifiedName(cdClass, compUnit)))
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDInterfacesList().stream()
        .filter(
            cdInterface ->
                existsHandwrittenClass(hwPath, determineQualifiedName(cdInterface, compUnit)))
        .forEach(this::applyTopMechanism);

    compUnit.getCDDefinition().getCDEnumsList().stream()
        .filter(cdEnum -> existsHandwrittenClass(hwPath, determineQualifiedName(cdEnum, compUnit)))
        .forEach(this::applyTopMechanism);

    return compUnit;
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

  protected class DetermineNameVisitor implements CDBasisVisitor2, CDInterfaceAndEnumVisitor2 {

    protected String packageName = "";

    @Override
    public void visit(ASTCDPackage node) {
      packageName = node.getName();
    }

    @Override
    public void endVisit(ASTCDPackage node) {
      packageName = "";
    }

    @Override
    public void visit(ASTCDClass node) {
      nameMap.put(node, packageName);
    }

    @Override
    public void visit(ASTCDInterface node) {
      nameMap.put(node, packageName);
    }

    @Override
    public void visit(ASTCDEnum node) {
      nameMap.put(node, packageName);
    }
  }

}
