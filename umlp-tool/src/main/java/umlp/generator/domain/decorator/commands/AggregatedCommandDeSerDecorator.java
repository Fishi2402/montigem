/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator.commands;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp._visitor.UMLPTraverser;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.CommandDecorator.DE_SER;

public class AggregatedCommandDeSerDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandDeSer";
  private final List<ASTCDClass> deSers;

  public AggregatedCommandDeSerDecorator(GlobalExtensionManagement glex, List<ASTCDClass> deSers) {
    super(glex);
    this.deSers = deSers;
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createDeSer(origCD, decoratedCD));
  }

  protected ASTCDClass createDeSer(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    String className = origCD.getCDDefinition().getName() + SUFFIX;

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(getExtendUsageFacade().createCDExtendUsage("AggregatedCommandDeSer"))
        .setName(className)
        .build();

    List<String> commandList = new ArrayList<>();

    // No Symbol table available
    UMLPTraverser t = UMLPMill.traverser();
    t.add4CDBasis(new CDBasisVisitor2() {
      ASTCDPackage p = null;
      public void visit(ASTCDPackage node) { p = node; }
      public void endVisit(ASTCDPackage node) { p = null; }
      public void visit(ASTCDClass node){
        if(deSers.contains(node)){
          String baseName = node.getName().replaceAll("DeSer$", "");
          if(p == null){
            commandList.add(getPackageName(origCD, DEFAULT_PACKAGE) + "." + baseName);
          }else{
            commandList.add(p.getName() + "." + baseName);
          }
        }
      }
    });
    decoratedCD.accept(t);

    getCD4C().addConstructor(ast, getConstructorTemplate(), commandList, className, getPackageName(origCD, DEFAULT_PACKAGE));
    getCD4C().addImport(ast, PathUtil.getBackendRTECommonPath("AggregatedCommandDeSer"));

    return ast;
  }

  protected String getConstructorTemplate() {
    return "umlp.commands.aggregatedcommanddeser.Constructor";
  }
}
