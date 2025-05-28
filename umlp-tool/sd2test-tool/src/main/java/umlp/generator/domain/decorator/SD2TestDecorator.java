package umlp.generator.domain.decorator;

import de.monticore.ast.ASTNode;
import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development._ast.ASTSD4DevelopmentNode;
import de.monticore.lang.sd4development._ast.ASTSDCall;
import de.monticore.lang.sd4development._ast.ASTSDClass;
import de.monticore.lang.sd4development._ast.ASTSDCondition;
import de.monticore.lang.sd4development._ast.ASTSDEndCall;
import de.monticore.lang.sd4development._ast.ASTSDEndCallArrow;
import de.monticore.lang.sd4development._ast.ASTSDIncompleteExpression;
import de.monticore.lang.sd4development._ast.ASTSDNew;
import de.monticore.lang.sd4development._ast.ASTSDReturn;
import de.monticore.lang.sd4development._ast.ASTSDThrow;
import de.monticore.lang.sd4development._ast.ASTSDVariableDeclaration;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentArtifactScope;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentGlobalScope;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentScope;
import de.monticore.lang.sd4development._visitor.SD4DevelopmentVisitor2;
import de.monticore.lang.sdbasis._ast.ASTSDAction;
import de.monticore.lang.sdbasis._ast.ASTSDActivityBar;
import de.monticore.lang.sdbasis._ast.ASTSDArtifact;
import de.monticore.lang.sdbasis._ast.ASTSDBasisNode;
import de.monticore.lang.sdbasis._ast.ASTSDBody;
import de.monticore.lang.sdbasis._ast.ASTSDCompleteModifier;
import de.monticore.lang.sdbasis._ast.ASTSDElement;
import de.monticore.lang.sdbasis._ast.ASTSDFreeModifier;
import de.monticore.lang.sdbasis._ast.ASTSDInitialModifier;
import de.monticore.lang.sdbasis._ast.ASTSDInteraction;
import de.monticore.lang.sdbasis._ast.ASTSDModifier;
import de.monticore.lang.sdbasis._ast.ASTSDObject;
import de.monticore.lang.sdbasis._ast.ASTSDObjectSource;
import de.monticore.lang.sdbasis._ast.ASTSDObjectTarget;
import de.monticore.lang.sdbasis._ast.ASTSDSendMessage;
import de.monticore.lang.sdbasis._ast.ASTSDSource;
import de.monticore.lang.sdbasis._ast.ASTSDTarget;
import de.monticore.lang.sdbasis._ast.ASTSDVisibleModifier;
import de.monticore.lang.sdbasis._ast.ASTSequenceDiagram;
import de.monticore.lang.sdbasis._symboltable.ISDBasisArtifactScope;
import de.monticore.lang.sdbasis._symboltable.ISDBasisGlobalScope;
import de.monticore.lang.sdbasis._symboltable.ISDBasisScope;
import de.monticore.lang.sdbasis._visitor.SDBasisVisitor2;
import de.monticore.ocl.oclexpressions._ast.ASTAnyExpression;
import de.monticore.ocl.oclexpressions._ast.ASTEquivalentExpression;
import de.monticore.ocl.oclexpressions._ast.ASTExistsExpression;
import de.monticore.ocl.oclexpressions._ast.ASTForallExpression;
import de.monticore.ocl.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.ocl.oclexpressions._ast.ASTImpliesExpression;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTIterateExpression;
import de.monticore.ocl.oclexpressions._ast.ASTLetinExpression;
import de.monticore.ocl.oclexpressions._ast.ASTOCLAtPreQualification;
import de.monticore.ocl.oclexpressions._ast.ASTOCLExpressionsNode;
import de.monticore.ocl.oclexpressions._ast.ASTOCLTransitiveQualification;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfThenExpression;
import de.monticore.ocl.oclexpressions._symboltable.IOCLExpressionsArtifactScope;
import de.monticore.ocl.oclexpressions._symboltable.IOCLExpressionsGlobalScope;
import de.monticore.ocl.oclexpressions._symboltable.IOCLExpressionsScope;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.Joiners;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;

public class SD2TestDecorator {

  private final GlobalExtensionManagement glex;

  public SD2TestDecorator(GlobalExtensionManagement glex) {
    this.glex = glex;
  }

  public void transform(ASTSDArtifact sdAst,
      ASTCDDefinition domainCd,
      ASTCDCompilationUnit decoratedCD) {
    var name = sdAst.getSequenceDiagram().getName();
    var targetPackage = getPackage(name.toLowerCase(), decoratedCD);
    targetPackage.addCDElement(createTest(sdAst, domainCd));
  }

  private ASTCDElement createTest(ASTSDArtifact model, ASTCDDefinition domainCd) {
    var name = model.getSequenceDiagram().getName();
    var domainName = domainCd.getName();
    var domainPackageName = domainName.toLowerCase();

    var testClass = CD4CodeBasisMill
        .cDClassBuilder()
        .setModifier(CDModifier.PUBLIC.build())
        .setName(name)
        .setCDExtendUsage(CDExtendUsageFacade
                              .getInstance()
                              .createCDExtendUsage(domainName + "SQLDBTestSetup"))
        .build();

    var cd4c = CD4C.getInstance();

    //constructor
    cd4c.addConstructor(testClass, "umlp.domain.sd2test.Constructor", name);

    //override initBackendManager
    cd4c.addMethod(testClass, "umlp.domain.sd2test.InitBackendManager", domainName);

    //TODO also visit the object diagram if present, to check which objects are pre-defined; Can Nico help me here?

    // apply a visitor to the model
    var traverser = SD4DevelopmentMill.traverser();
    var visitor = new LoggingSDVisitor();
    traverser.add4SDBasis(visitor);
    traverser.add4SD4Development(visitor);
    model.accept(traverser); //TODO create instructions based on what we visit
    visitor.visits.forEach(System.out::println);

    //TODO for starters, just create overridable createFoo() methods for every object needed for the
    // test
    //TODO Test.ftl with objects and instructions as parameters

    //imports
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(domainPackageName, "test_infrastructure", "*"));
    cd4c.addImport(testClass, Joiners.DOT.join(PathUtil.getTestInfrastructurePath(), "*"));

    return testClass;
  }

  private ASTCDPackage getPackage(String packageName, ASTCDCompilationUnit decoratedCD) {
    var p = CD4CodeMill
        .cDPackageBuilder()
        .setMCQualifiedName(MCTypeFacade.getInstance().createQualifiedName(packageName))
        .build();
    decoratedCD.getCDDefinition().addCDElement(p);
    return p;
  }

  private static class LoggingSDVisitor
      implements SDBasisVisitor2, SD4DevelopmentVisitor2, OCLExpressionsVisitor2 {

    private final List<String> visits = new ArrayList<>();

    @Override
    public void visit(ASTSDCall node) {visits.add("visit ASTSDCall");}

    @Override
    public void endVisit(ASTSDCall node) {visits.add("end ASTSDCall");}

    @Override
    public void visit(ASTSDNew node) {visits.add("visit ASTSDNew");}

    @Override
    public void endVisit(ASTSDNew node) {visits.add("end ASTSDNew");}

    @Override
    public void visit(ASTSDReturn node) {visits.add("visit ASTSDReturn");}

    @Override
    public void endVisit(ASTSDReturn node) {visits.add("end ASTSDReturn");}

    @Override
    public void visit(ASTSDThrow node) {visits.add("visit ASTSDThrow");}

    @Override
    public void endVisit(ASTSDThrow node) {visits.add("end ASTSDThrow");}

    @Override
    public void visit(ASTSDEndCall node) {visits.add("visit ASTSDEndCall");}

    @Override
    public void endVisit(ASTSDEndCall node) {visits.add("end ASTSDEndCall");}

    @Override
    public void visit(ASTSDEndCallArrow node) {visits.add("visit ASTSDEndCallArrow");}

    @Override
    public void endVisit(ASTSDEndCallArrow node) {visits.add("end ASTSDEndCallArrow");}

    @Override
    public void visit(ASTSDIncompleteExpression node) {visits.add("visit ASTSDIncompleteExpression");}

    @Override
    public void endVisit(ASTSDIncompleteExpression node) {visits.add("end ASTSDIncompleteExpression");}

    @Override
    public void visit(ASTSDClass node) {visits.add("visit ASTSDClass");}

    @Override
    public void endVisit(ASTSDClass node) {visits.add("end ASTSDClass");}

    @Override
    public void visit(ASTSDCondition node) {visits.add("visit ASTSDCondition");}

    @Override
    public void endVisit(ASTSDCondition node) {visits.add("end ASTSDCondition");}

    @Override
    public void visit(ASTSDVariableDeclaration node) {visits.add("visit ASTSDVariableDeclaration");}

    @Override
    public void endVisit(ASTSDVariableDeclaration node) {visits.add("end ASTSDVariableDeclaration");}

    @Override
    public void visit(ASTSD4DevelopmentNode node) {visits.add("visit ASTSD4DevelopmentNode");}

    @Override
    public void endVisit(ASTSD4DevelopmentNode node) {visits.add("end ASTSD4DevelopmentNode");}

    @Override
    public void visit(ISD4DevelopmentScope node) {visits.add("visit ISD4DevelopmentScope");}

    @Override
    public void endVisit(ISD4DevelopmentScope node) {visits.add("end ISD4DevelopmentScope");}

    @Override
    public void visit(ISD4DevelopmentArtifactScope node) {visits.add("visit ISD4DevelopmentArtifactScope");}

    @Override
    public void endVisit(ISD4DevelopmentArtifactScope node) {visits.add("end ISD4DevelopmentArtifactScope");}

    @Override
    public void visit(ISD4DevelopmentGlobalScope node) {visits.add("visit ISD4DevelopmentGlobalScope");}

    @Override
    public void endVisit(ISD4DevelopmentGlobalScope node) {visits.add("end ISD4DevelopmentGlobalScope");}

    @Override
    public void visit(ASTSDArtifact node) {visits.add("visit ASTSDArtifact");}

    @Override
    public void endVisit(ASTSDArtifact node) {visits.add("end ASTSDArtifact");}

    @Override
    public void visit(ASTSequenceDiagram node) {visits.add("visit ASTSequenceDiagram");}

    @Override
    public void endVisit(ASTSequenceDiagram node) {visits.add("end ASTSequenceDiagram");}

    @Override
    public void visit(ASTSDBody node) {visits.add("visit ASTSDBody");}

    @Override
    public void endVisit(ASTSDBody node) {visits.add("end ASTSDBody");}

    @Override
    public void visit(ASTSDCompleteModifier node) {visits.add("visit ASTSDCompleteModifier");}

    @Override
    public void endVisit(ASTSDCompleteModifier node) {visits.add("end ASTSDCompleteModifier");}

    @Override
    public void visit(ASTSDFreeModifier node) {visits.add("visit ASTSDFreeModifier");}

    @Override
    public void endVisit(ASTSDFreeModifier node) {visits.add("end ASTSDFreeModifier");}

    @Override
    public void visit(ASTSDInitialModifier node) {visits.add("visit ASTSDInitialModifier");}

    @Override
    public void endVisit(ASTSDInitialModifier node) {visits.add("end ASTSDInitialModifier");}

    @Override
    public void visit(ASTSDVisibleModifier node) {visits.add("visit ASTSDVisibleModifier");}

    @Override
    public void endVisit(ASTSDVisibleModifier node) {visits.add("end ASTSDVisibleModifier");}

    @Override
    public void visit(ASTSDObject node) {visits.add("visit ASTSDObject");}

    @Override
    public void endVisit(ASTSDObject node) {visits.add("end ASTSDObject");}

    @Override
    public void visit(ASTSDObjectSource node) {visits.add("visit ASTSDObjectSource");}

    @Override
    public void endVisit(ASTSDObjectSource node) {visits.add("end ASTSDObjectSource");}

    @Override
    public void visit(ASTSDObjectTarget node) {visits.add("visit ASTSDObjectTarget");}

    @Override
    public void endVisit(ASTSDObjectTarget node) {visits.add("end ASTSDObjectTarget");}

    @Override
    public void visit(ASTSDSendMessage node) {visits.add("visit ASTSDSendMessage");}

    @Override
    public void endVisit(ASTSDSendMessage node) {visits.add("end ASTSDSendMessage");}

    @Override
    public void visit(ASTSDActivityBar node) {visits.add("visit ASTSDActivityBar");}

    @Override
    public void endVisit(ASTSDActivityBar node) {visits.add("end ASTSDActivityBar");}

    @Override
    public void visit(ASTSDModifier node) {visits.add("visit ASTSDModifier");}

    @Override
    public void endVisit(ASTSDModifier node) {visits.add("end ASTSDModifier");}

    @Override
    public void visit(ASTSDElement node) {visits.add("visit ASTSDElement");}

    @Override
    public void endVisit(ASTSDElement node) {visits.add("end ASTSDElement");}

    @Override
    public void visit(ASTSDInteraction node) {visits.add("visit ASTSDInteraction");}

    @Override
    public void endVisit(ASTSDInteraction node) {visits.add("end ASTSDInteraction");}

    @Override
    public void visit(ASTSDSource node) {visits.add("visit ASTSDSource");}

    @Override
    public void endVisit(ASTSDSource node) {visits.add("end ASTSDSource");}

    @Override
    public void visit(ASTSDTarget node) {visits.add("visit ASTSDTarget");}

    @Override
    public void endVisit(ASTSDTarget node) {visits.add("end ASTSDTarget");}

    @Override
    public void visit(ASTSDAction node) {visits.add("visit ASTSDAction");}

    @Override
    public void endVisit(ASTSDAction node) {visits.add("end ASTSDAction");}

    @Override
    public void visit(ASTSDBasisNode node) {visits.add("visit ASTSDBasisNode");}

    @Override
    public void endVisit(ASTSDBasisNode node) {visits.add("end ASTSDBasisNode");}

    @Override
    public void visit(ISDBasisScope node) {visits.add("visit ISDBasisScope");}

    @Override
    public void endVisit(ISDBasisScope node) {visits.add("end ISDBasisScope");}

    @Override
    public void visit(ISDBasisArtifactScope node) {visits.add("visit ISDBasisArtifactScope");}

    @Override
    public void endVisit(ISDBasisArtifactScope node) {visits.add("end ISDBasisArtifactScope");}

    @Override
    public void visit(ISDBasisGlobalScope node) {visits.add("visit ISDBasisGlobalScope");}

    @Override
    public void endVisit(ISDBasisGlobalScope node) {visits.add("end ISDBasisGlobalScope");}

    @Override
    public void visit(ASTOCLVariableDeclaration node) {visits.add("visit ASTOCLVariableDeclaration");}

    @Override
    public void endVisit(ASTOCLVariableDeclaration node) {visits.add("end ASTOCLVariableDeclaration");}

    @Override
    public void visit(ASTTypeIfExpression node) {visits.add("visit ASTTypeIfExpression");}

    @Override
    public void endVisit(ASTTypeIfExpression node) {visits.add("end ASTTypeIfExpression");}

    @Override
    public void visit(ASTTypeIfThenExpression node) {visits.add("visit ASTTypeIfThenExpression");}

    @Override
    public void endVisit(ASTTypeIfThenExpression node) {visits.add("end ASTTypeIfThenExpression");}

    @Override
    public void visit(ASTIfThenElseExpression node) {visits.add("visit ASTIfThenElseExpression");}

    @Override
    public void endVisit(ASTIfThenElseExpression node) {visits.add("end ASTIfThenElseExpression");}

    @Override
    public void visit(ASTImpliesExpression node) {visits.add("visit ASTImpliesExpression");}

    @Override
    public void endVisit(ASTImpliesExpression node) {visits.add("end ASTImpliesExpression");}

    @Override
    public void visit(ASTEquivalentExpression node) {visits.add("visit ASTEquivalentExpression");}

    @Override
    public void endVisit(ASTEquivalentExpression node) {visits.add("end ASTEquivalentExpression");}

    @Override
    public void visit(ASTForallExpression node) {visits.add("visit ASTForallExpression");}

    @Override
    public void endVisit(ASTForallExpression node) {visits.add("end ASTForallExpression");}

    @Override
    public void visit(ASTExistsExpression node) {visits.add("visit ASTExistsExpression");}

    @Override
    public void endVisit(ASTExistsExpression node) {visits.add("end ASTExistsExpression");}

    @Override
    public void visit(ASTAnyExpression node) {visits.add("visit ASTAnyExpression");}

    @Override
    public void endVisit(ASTAnyExpression node) {visits.add("end ASTAnyExpression");}

    @Override
    public void visit(ASTLetinExpression node) {visits.add("visit ASTLetinExpression");}

    @Override
    public void endVisit(ASTLetinExpression node) {visits.add("end ASTLetinExpression");}

    @Override
    public void visit(ASTIterateExpression node) {visits.add("visit ASTIterateExpression");}

    @Override
    public void endVisit(ASTIterateExpression node) {visits.add("end ASTIterateExpression");}

    @Override
    public void visit(ASTInDeclaration node) {visits.add("visit ASTInDeclaration");}

    @Override
    public void endVisit(ASTInDeclaration node) {visits.add("end ASTInDeclaration");}

    @Override
    public void visit(ASTInDeclarationVariable node) {visits.add("visit ASTInDeclarationVariable");}

    @Override
    public void endVisit(ASTInDeclarationVariable node) {visits.add("end ASTInDeclarationVariable");}

    @Override
    public void visit(ASTOCLAtPreQualification node) {visits.add("visit ASTOCLAtPreQualification");}

    @Override
    public void endVisit(ASTOCLAtPreQualification node) {visits.add("end ASTOCLAtPreQualification");}

    @Override
    public void visit(ASTOCLTransitiveQualification node) {visits.add("visit ASTOCLTransitiveQualification");}

    @Override
    public void endVisit(ASTOCLTransitiveQualification node) {visits.add("end ASTOCLTransitiveQualification");}

    @Override
    public void visit(ASTOCLExpressionsNode node) {visits.add("visit ASTOCLExpressionsNode");}

    @Override
    public void endVisit(ASTOCLExpressionsNode node) {visits.add("end ASTOCLExpressionsNode");}

    @Override
    public void visit(IOCLExpressionsScope node) {visits.add("visit IOCLExpressionsScope");}

    @Override
    public void endVisit(IOCLExpressionsScope node) {visits.add("end IOCLExpressionsScope");}

    @Override
    public void visit(IOCLExpressionsArtifactScope node) {visits.add("visit IOCLExpressionsArtifactScope");}

    @Override
    public void endVisit(IOCLExpressionsArtifactScope node) {visits.add("end IOCLExpressionsArtifactScope");}

    @Override
    public void visit(IOCLExpressionsGlobalScope node) {visits.add("visit IOCLExpressionsGlobalScope");}

    @Override
    public void endVisit(IOCLExpressionsGlobalScope node) {visits.add("end IOCLExpressionsGlobalScope");}

    @Override
    public void endVisit(ASTNode node) {visits.add("end ASTNode");}

    @Override
    public void visit(ASTNode node) {visits.add("visit ASTNode");}

    @Override
    public void visit(ISymbol symbol) {
      SDBasisVisitor2.super.visit(symbol);
    }

    @Override
    public void endVisit(ISymbol symbol) {
      SDBasisVisitor2.super.endVisit(symbol);
    }

    @Override
    public void visit(IScope scope) {
      SDBasisVisitor2.super.visit(scope);
    }

    @Override
    public void endVisit(IScope scope) {
      SDBasisVisitor2.super.endVisit(scope);
    }

    @Override
    public void visit(IScopeSpanningSymbol symbol) {
      SDBasisVisitor2.super.visit(symbol);
    }

    @Override
    public void endVisit(IScopeSpanningSymbol symbol) {
      SDBasisVisitor2.super.endVisit(symbol);
    }
  }
}
