package umlp.generator.domain.visitor;

import de.monticore.ast.ASTNode;
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
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesArtifactScope;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesGlobalScope;
import de.monticore.types.mcbasictypes._symboltable.IMCBasicTypesScope;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;

import java.util.ArrayList;
import java.util.List;

//TODO better move this to its own class
public final class LoggingSDVisitor implements SDBasisVisitor2,
                                               SD4DevelopmentVisitor2,
                                               OCLExpressionsVisitor2,
                                               MCBasicTypesVisitor2 {

  private final List<String> visits = new ArrayList<>();

  public List<String> visits() {
    return visits;
  }

  @Override
  public void visit(ASTSDCall node) {
    visits.add("visit ASTSDCall");
  }

  @Override
  public void endVisit(ASTSDCall node) {
    visits.add("end visit ASTSDCall");
  }

  @Override
  public void visit(ASTSDNew node) {
    visits.add("visit ASTSDNew");
  }

  @Override
  public void endVisit(ASTSDNew node) {
    visits.add("end visit ASTSDNew");
  }

  @Override
  public void visit(ASTSDReturn node) {
    visits.add("visit ASTSDReturn");
  }

  @Override
  public void endVisit(ASTSDReturn node) {
    visits.add("end visit ASTSDReturn");
  }

  @Override
  public void visit(ASTSDThrow node) {
    visits.add("visit ASTSDThrow");
  }

  @Override
  public void endVisit(ASTSDThrow node) {
    visits.add("end visit ASTSDThrow");
  }

  @Override
  public void visit(ASTSDEndCall node) {
    visits.add("visit ASTSDEndCall");
  }

  @Override
  public void endVisit(ASTSDEndCall node) {
    visits.add("end visit ASTSDEndCall");
  }

  @Override
  public void visit(ASTSDEndCallArrow node) {
    visits.add("visit ASTSDEndCallArrow");
  }

  @Override
  public void endVisit(ASTSDEndCallArrow node) {
    visits.add("end visit ASTSDEndCallArrow");
  }

  @Override
  public void visit(ASTSDIncompleteExpression node) {
    visits.add("visit ASTSDIncompleteExpression");
  }

  @Override
  public void endVisit(ASTSDIncompleteExpression node) {
    visits.add("end visit ASTSDIncompleteExpression");
  }

  @Override
  public void visit(ASTSDClass node) {
    visits.add("visit ASTSDClass");
  }

  @Override
  public void endVisit(ASTSDClass node) {
    visits.add("end visit ASTSDClass");
  }

  @Override
  public void visit(ASTSDCondition node) {
    visits.add("visit ASTSDCondition");
  }

  @Override
  public void endVisit(ASTSDCondition node) {
    visits.add("end visit ASTSDCondition");
  }

  @Override
  public void visit(ASTSDVariableDeclaration node) {
    visits.add("visit ASTSDVariableDeclaration");
  }

  @Override
  public void endVisit(ASTSDVariableDeclaration node) {
    visits.add("end visit ASTSDVariableDeclaration");
  }

  @Override
  public void visit(ASTSD4DevelopmentNode node) {
    visits.add("visit ASTSD4DevelopmentNode");
  }

  @Override
  public void endVisit(ASTSD4DevelopmentNode node) {
    visits.add("end visit ASTSD4DevelopmentNode");
  }

  @Override
  public void visit(ISD4DevelopmentScope node) {
    visits.add("visit ISD4DevelopmentScope");
  }

  @Override
  public void endVisit(ISD4DevelopmentScope node) {
    visits.add("end visit ISD4DevelopmentScope");
  }

  @Override
  public void visit(ISD4DevelopmentArtifactScope node) {
    visits.add("visit ISD4DevelopmentArtifactScope");
  }

  @Override
  public void endVisit(ISD4DevelopmentArtifactScope node) {
    visits.add("end visit ISD4DevelopmentArtifactScope");
  }

  @Override
  public void visit(ISD4DevelopmentGlobalScope node) {
    visits.add("visit ISD4DevelopmentGlobalScope");
  }

  @Override
  public void endVisit(ISD4DevelopmentGlobalScope node) {
    visits.add("end visit ISD4DevelopmentGlobalScope");
  }

  @Override
  public void visit(ASTSDArtifact node) {
    visits.add("visit ASTSDArtifact");
  }

  @Override
  public void endVisit(ASTSDArtifact node) {
    visits.add("end visit ASTSDArtifact");
  }

  @Override
  public void visit(ASTSequenceDiagram node) {
    visits.add("visit ASTSequenceDiagram");
  }

  @Override
  public void endVisit(ASTSequenceDiagram node) {
    visits.add("end visit ASTSequenceDiagram");
  }

  @Override
  public void visit(ASTSDBody node) {
    visits.add("visit ASTSDBody");
  }

  @Override
  public void endVisit(ASTSDBody node) {
    visits.add("end visit ASTSDBody");
  }

  @Override
  public void visit(ASTSDCompleteModifier node) {
    visits.add("visit ASTSDCompleteModifier");
  }

  @Override
  public void endVisit(ASTSDCompleteModifier node) {
    visits.add("end visit ASTSDCompleteModifier");
  }

  @Override
  public void visit(ASTSDFreeModifier node) {
    visits.add("visit ASTSDFreeModifier");
  }

  @Override
  public void endVisit(ASTSDFreeModifier node) {
    visits.add("end visit ASTSDFreeModifier");
  }

  @Override
  public void visit(ASTSDInitialModifier node) {
    visits.add("visit ASTSDInitialModifier");
  }

  @Override
  public void endVisit(ASTSDInitialModifier node) {
    visits.add("end visit ASTSDInitialModifier");
  }

  @Override
  public void visit(ASTSDVisibleModifier node) {
    visits.add("visit ASTSDVisibleModifier");
  }

  @Override
  public void endVisit(ASTSDVisibleModifier node) {
    visits.add("end visit ASTSDVisibleModifier");
  }

  @Override
  public void visit(ASTSDObject node) {
    visits.add("visit ASTSDObject");
  }

  @Override
  public void endVisit(ASTSDObject node) {
    visits.add("end visit ASTSDObject");
  }

  @Override
  public void visit(ASTSDObjectSource node) {
    visits.add("visit ASTSDObjectSource");
  }

  @Override
  public void endVisit(ASTSDObjectSource node) {
    visits.add("end visit ASTSDObjectSource");
  }

  @Override
  public void visit(ASTSDObjectTarget node) {
    visits.add("visit ASTSDObjectTarget");
  }

  @Override
  public void endVisit(ASTSDObjectTarget node) {
    visits.add("end visit ASTSDObjectTarget");
  }

  @Override
  public void visit(ASTSDSendMessage node) {
    visits.add("visit ASTSDSendMessage");
  }

  @Override
  public void endVisit(ASTSDSendMessage node) {
    visits.add("end visit ASTSDSendMessage");
  }

  @Override
  public void visit(ASTSDActivityBar node) {
    visits.add("visit ASTSDActivityBar");
  }

  @Override
  public void endVisit(ASTSDActivityBar node) {
    visits.add("end visit ASTSDActivityBar");
  }

  @Override
  public void visit(ASTSDModifier node) {
    visits.add("visit ASTSDModifier");
  }

  @Override
  public void endVisit(ASTSDModifier node) {
    visits.add("end visit ASTSDModifier");
  }

  @Override
  public void visit(ASTSDElement node) {
    visits.add("visit ASTSDElement");
  }

  @Override
  public void endVisit(ASTSDElement node) {
    visits.add("end visit ASTSDElement");
  }

  @Override
  public void visit(ASTSDInteraction node) {
    visits.add("visit ASTSDInteraction");
  }

  @Override
  public void endVisit(ASTSDInteraction node) {
    visits.add("end visit ASTSDInteraction");
  }

  @Override
  public void visit(ASTSDSource node) {
    visits.add("visit ASTSDSource");
  }

  @Override
  public void endVisit(ASTSDSource node) {
    visits.add("end visit ASTSDSource");
  }

  @Override
  public void visit(ASTSDTarget node) {
    visits.add("visit ASTSDTarget");
  }

  @Override
  public void endVisit(ASTSDTarget node) {
    visits.add("end visit ASTSDTarget");
  }

  @Override
  public void visit(ASTSDAction node) {
    visits.add("visit ASTSDAction");
  }

  @Override
  public void endVisit(ASTSDAction node) {
    visits.add("end visit ASTSDAction");
  }

  @Override
  public void visit(ASTSDBasisNode node) {
    visits.add("visit ASTSDBasisNode");
  }

  @Override
  public void endVisit(ASTSDBasisNode node) {
    visits.add("end visit ASTSDBasisNode");
  }

  @Override
  public void visit(ISDBasisScope node) {
    visits.add("visit ISDBasisScope");
  }

  @Override
  public void endVisit(ISDBasisScope node) {
    visits.add("end visit ISDBasisScope");
  }

  @Override
  public void visit(ISDBasisArtifactScope node) {
    visits.add("visit ISDBasisArtifactScope");
  }

  @Override
  public void endVisit(ISDBasisArtifactScope node) {
    visits.add("end visit ISDBasisArtifactScope");
  }

  @Override
  public void visit(ISDBasisGlobalScope node) {
    visits.add("visit ISDBasisGlobalScope");
  }

  @Override
  public void endVisit(ISDBasisGlobalScope node) {
    visits.add("end visit ISDBasisGlobalScope");
  }

  @Override
  public void visit(ASTOCLVariableDeclaration node) {
    visits.add("visit ASTOCLVariableDeclaration");
  }

  @Override
  public void endVisit(ASTOCLVariableDeclaration node) {
    visits.add("end visit ASTOCLVariableDeclaration");
  }

  @Override
  public void visit(ASTTypeIfExpression node) {
    visits.add("visit ASTTypeIfExpression");
  }

  @Override
  public void endVisit(ASTTypeIfExpression node) {
    visits.add("end visit ASTTypeIfExpression");
  }

  @Override
  public void visit(ASTTypeIfThenExpression node) {
    visits.add("visit ASTTypeIfThenExpression");
  }

  @Override
  public void endVisit(ASTTypeIfThenExpression node) {
    visits.add("end visit ASTTypeIfThenExpression");
  }

  @Override
  public void visit(ASTIfThenElseExpression node) {
    visits.add("visit ASTIfThenElseExpression");
  }

  @Override
  public void endVisit(ASTIfThenElseExpression node) {
    visits.add("end visit ASTIfThenElseExpression");
  }

  @Override
  public void visit(ASTImpliesExpression node) {
    visits.add("visit ASTImpliesExpression");
  }

  @Override
  public void endVisit(ASTImpliesExpression node) {
    visits.add("end visit ASTImpliesExpression");
  }

  @Override
  public void visit(ASTEquivalentExpression node) {
    visits.add("visit ASTEquivalentExpression");
  }

  @Override
  public void endVisit(ASTEquivalentExpression node) {
    visits.add("end visit ASTEquivalentExpression");
  }

  @Override
  public void visit(ASTForallExpression node) {
    visits.add("visit ASTForallExpression");
  }

  @Override
  public void endVisit(ASTForallExpression node) {
    visits.add("end visit ASTForallExpression");
  }

  @Override
  public void visit(ASTExistsExpression node) {
    visits.add("visit ASTExistsExpression");
  }

  @Override
  public void endVisit(ASTExistsExpression node) {
    visits.add("end visit ASTExistsExpression");
  }

  @Override
  public void visit(ASTAnyExpression node) {
    visits.add("visit ASTAnyExpression");
  }

  @Override
  public void endVisit(ASTAnyExpression node) {
    visits.add("end visit ASTAnyExpression");
  }

  @Override
  public void visit(ASTLetinExpression node) {
    visits.add("visit ASTLetinExpression");
  }

  @Override
  public void endVisit(ASTLetinExpression node) {
    visits.add("end visit ASTLetinExpression");
  }

  @Override
  public void visit(ASTIterateExpression node) {
    visits.add("visit ASTIterateExpression");
  }

  @Override
  public void endVisit(ASTIterateExpression node) {
    visits.add("end visit ASTIterateExpression");
  }

  @Override
  public void visit(ASTInDeclaration node) {
    visits.add("visit ASTInDeclaration");
  }

  @Override
  public void endVisit(ASTInDeclaration node) {
    visits.add("end visit ASTInDeclaration");
  }

  @Override
  public void visit(ASTInDeclarationVariable node) {
    visits.add("visit ASTInDeclarationVariable");
  }

  @Override
  public void endVisit(ASTInDeclarationVariable node) {
    visits.add("end visit ASTInDeclarationVariable");
  }

  @Override
  public void visit(ASTOCLAtPreQualification node) {
    visits.add("visit ASTOCLAtPreQualification");
  }

  @Override
  public void endVisit(ASTOCLAtPreQualification node) {
    visits.add("end visit ASTOCLAtPreQualification");
  }

  @Override
  public void visit(ASTOCLTransitiveQualification node) {
    visits.add("visit ASTOCLTransitiveQualification");
  }

  @Override
  public void endVisit(ASTOCLTransitiveQualification node) {
    visits.add("end visit ASTOCLTransitiveQualification");
  }

  @Override
  public void visit(ASTOCLExpressionsNode node) {
    visits.add("visit ASTOCLExpressionsNode");
  }

  @Override
  public void endVisit(ASTOCLExpressionsNode node) {
    visits.add("end visit ASTOCLExpressionsNode");
  }

  @Override
  public void visit(IOCLExpressionsScope node) {
    visits.add("visit IOCLExpressionsScope");
  }

  @Override
  public void endVisit(IOCLExpressionsScope node) {
    visits.add("end visit IOCLExpressionsScope");
  }

  @Override
  public void visit(IOCLExpressionsArtifactScope node) {
    visits.add("visit IOCLExpressionsArtifactScope");
  }

  @Override
  public void endVisit(IOCLExpressionsArtifactScope node) {
    visits.add("end visit IOCLExpressionsArtifactScope");
  }

  @Override
  public void visit(IOCLExpressionsGlobalScope node) {
    visits.add("visit IOCLExpressionsGlobalScope");
  }

  @Override
  public void endVisit(IOCLExpressionsGlobalScope node) {
    visits.add("end visit IOCLExpressionsGlobalScope");
  }

  @Override
  public void endVisit(ASTNode node) {
    visits.add("end visit ASTNode");
  }

  @Override
  public void visit(ASTNode node) {
    visits.add("visit ASTNode");
  }

  @Override
  public void visit(ISymbol symbol) {
    visits.add("visit ISymbol");
  }

  @Override
  public void endVisit(ISymbol symbol) {
    visits.add("end visit ISymbol");
  }

  @Override
  public void visit(IScope scope) {
    visits.add("visit IScope");
  }

  @Override
  public void endVisit(IScope scope) {
    visits.add("end visit IScope");
  }

  @Override
  public void visit(IScopeSpanningSymbol symbol) {
    visits.add("visit IScopeSpanningSymbol");
  }

  @Override
  public void endVisit(IScopeSpanningSymbol symbol) {
    visits.add("end visit IScopeSpanningSymbol");
  }

  @Override
  public void visit(ASTMCQualifiedName node) {
    visits.add("visit ASTMCQualifiedName");
  }

  @Override
  public void endVisit(ASTMCQualifiedName node) {
    visits.add("end visit ASTMCQualifiedName");
  }

  @Override
  public void visit(ASTMCPackageDeclaration node) {
    visits.add("visit ASTMCPackageDeclaration");
  }

  @Override
  public void endVisit(ASTMCPackageDeclaration node) {
    visits.add("end visit ASTMCPackageDeclaration");
  }

  @Override
  public void visit(ASTMCImportStatement node) {
    visits.add("visit ASTMCImportStatement");
  }

  @Override
  public void endVisit(ASTMCImportStatement node) {
    visits.add("end visit ASTMCImportStatement");
  }

  @Override
  public void visit(ASTMCPrimitiveType node) {
    visits.add("visit ASTMCPrimitiveType");
  }

  @Override
  public void endVisit(ASTMCPrimitiveType node) {
    visits.add("end visit ASTMCPrimitiveType");
  }

  @Override
  public void visit(ASTMCQualifiedType node) {
    visits.add("visit ASTMCQualifiedType");
  }

  @Override
  public void endVisit(ASTMCQualifiedType node) {
    visits.add("end visit ASTMCQualifiedType");
  }

  @Override
  public void visit(ASTMCReturnType node) {
    visits.add("visit ASTMCReturnType");
  }

  @Override
  public void endVisit(ASTMCReturnType node) {
    visits.add("end visit ASTMCReturnType");
  }

  @Override
  public void visit(ASTMCVoidType node) {
    visits.add("visit ASTMCVoidType");
  }

  @Override
  public void endVisit(ASTMCVoidType node) {
    visits.add("end visit ASTMCVoidType");
  }

  @Override
  public void visit(ASTMCType node) {
    visits.add("visit ASTMCType");
  }

  @Override
  public void endVisit(ASTMCType node) {
    visits.add("end visit ASTMCType");
  }

  @Override
  public void visit(ASTMCObjectType node) {
    visits.add("visit ASTMCObjectType");
  }

  @Override
  public void endVisit(ASTMCObjectType node) {
    visits.add("end visit ASTMCObjectType");
  }

  @Override
  public void visit(ASTMCBasicTypesNode node) {
    visits.add("visit ASTMCBasicTypesNode");
  }

  @Override
  public void endVisit(ASTMCBasicTypesNode node) {
    visits.add("end visit ASTMCBasicTypesNode");
  }

  @Override
  public void visit(IMCBasicTypesScope node) {
    visits.add("visit IMCBasicTypesScope");
  }

  @Override
  public void endVisit(IMCBasicTypesScope node) {
    visits.add("end visit IMCBasicTypesScope");
  }

  @Override
  public void visit(IMCBasicTypesArtifactScope node) {
    visits.add("visit IMCBasicTypesArtifactScope");
  }

  @Override
  public void endVisit(IMCBasicTypesArtifactScope node) {
    visits.add("end visit IMCBasicTypesArtifactScope");
  }

  @Override
  public void visit(IMCBasicTypesGlobalScope node) {
    visits.add("visit IMCBasicTypesGlobalScope");
  }

  @Override
  public void endVisit(IMCBasicTypesGlobalScope node) {
    visits.add("end visit IMCBasicTypesGlobalScope");
  }
}
