package umlp;

import de.monticore.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._ast.ASTCD4CodeNode;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._ast.ASTCD4CodeBasisNode;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociationNode;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceAndEnumNode;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTMCLiteralsBasisNode;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.*;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;
import umlp._ast.ASTUMLPNode;
import umlp._visitor.UMLPTraverser;

public class UMLPFullPrettyPrinter {
    protected UMLPTraverser traverser;
    protected IndentPrinter printer;
    protected boolean printComments = true;

    public UMLPFullPrettyPrinter() {
        this(new IndentPrinter());
    }

    public UMLPFullPrettyPrinter(IndentPrinter printer) {
        this.printer = printer;
        this.traverser = UMLPMill.traverser();

        UMLPPrettyPrinter umlp = new UMLPPrettyPrinter(printer);
        traverser.add4UMLP(umlp);
        traverser.setUMLPHandler(umlp);

        CDBasisPrettyPrinter cdBasis = new CDBasisPrettyPrinter(printer);
        traverser.add4CDBasis(cdBasis);
        traverser.setCDBasisHandler(cdBasis);

        CDInterfaceAndEnumPrettyPrinter cdInterface = new CDInterfaceAndEnumPrettyPrinter(printer);
        traverser.add4CDInterfaceAndEnum(cdInterface);
        traverser.setCDInterfaceAndEnumHandler(cdInterface);

        CDAssociationPrettyPrinter cdAssoc = new CDAssociationPrettyPrinter(printer);
        traverser.add4CDAssociation(cdAssoc);
        traverser.setCDAssociationHandler(cdAssoc);

        CD4CodeBasisPrettyPrinter cd4CodeBasis = new CD4CodeBasisPrettyPrinter(printer);
        traverser.add4CD4CodeBasis(cd4CodeBasis);
        traverser.setCD4CodeBasisHandler(cd4CodeBasis);

        MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
        traverser.add4MCBasicTypes(basicTypes);
        traverser.setMCBasicTypesHandler(basicTypes);

        UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
        traverser.add4UMLStereotype(umlStereotype);
        traverser.setUMLStereotypeHandler(umlStereotype);

        UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
        traverser.add4UMLModifier(umlModifier);
        traverser.setUMLModifierHandler(umlModifier);

        MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
        traverser.add4MCCollectionTypes(collectionTypes);
        traverser.setMCCollectionTypesHandler(collectionTypes);

        MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer);
        traverser.add4MCArrayTypes(arrayTypes);
        traverser.setMCArrayTypesHandler(arrayTypes);

        MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(printer);
        traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
        traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);

        MCFullGenericTypesPrettyPrinter fullGenericTypes = new MCFullGenericTypesPrettyPrinter(printer);
        traverser.add4MCFullGenericTypes(fullGenericTypes);
        traverser.setMCFullGenericTypesHandler(fullGenericTypes);

        ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
        traverser.add4ExpressionsBasis(expressionsBasis);
        traverser.setExpressionsBasisHandler(expressionsBasis);

        MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
        traverser.add4MCCommonLiterals(commonLiterals);
        traverser.setMCCommonLiteralsHandler(commonLiterals);

        BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
        traverser.add4BitExpressions(bitExpressions);
        traverser.setBitExpressionsHandler(bitExpressions);

        CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
        traverser.add4CommonExpressions(commonExpressions);
        traverser.setCommonExpressionsHandler(commonExpressions);
    }

    public IndentPrinter getPrinter() {
        return printer;
    }

    public void setPrinter(IndentPrinter printer) {
        this.printer = printer;
    }

    public void setTraverser(UMLPTraverser traverser) {
        this.traverser = traverser;
    }

    public UMLPTraverser getTraverser() {
        return traverser;
    }

    public String prettyprint(ASTCDCompilationUnit node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCDParameter node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCDBasisNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCDAssociationNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCDInterfaceAndEnumNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCD4AnalysisNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCD4CodeBasisNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTCD4CodeNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTMCLiteralsBasisNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTExpression node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTExpressionsBasisNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTMCBasicTypesNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTUMLStereotypeNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTUMLModifierNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

    public String prettyprint(ASTUMLPNode node) {
        getPrinter().clearBuffer();
        node.accept(getTraverser());
        return getPrinter().getContent();
    }

}
