package umlp;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import org.apache.tools.ant.taskdefs.optional.extension.LibFileSet;
import umlp._ast.*;
import umlp._visitor.UMLPHandler;
import umlp._visitor.UMLPTraverser;
import umlp._visitor.UMLPVisitor2;

import java.util.List;

public class UMLPPrettyPrinter implements UMLPVisitor2, UMLPHandler {

    protected UMLPTraverser traverser;
    protected IndentPrinter printer;

    @Override
    public UMLPTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(UMLPTraverser traverser) {
        this.traverser = traverser;
    }

    public IndentPrinter getPrinter() {
        return printer;
    }

    public void setPrinter(IndentPrinter printer) {
        this.printer = printer;
    }

    public UMLPPrettyPrinter(IndentPrinter indentPrinter) {
        this.printer = indentPrinter;
    }

    @Override
    public void handle(ASTUMLPDef node) {
        int kind = node.getKind();
        if (kind == ASTConstantsUMLP.UMLP ) {
            getPrinter().print("umlp");
        } else if (kind == ASTConstantsUMLP.CLASSDIGRAMM) {
            getPrinter().print("classdigramm");
        } else {
            getPrinter().print("ontology");
        }
        getPrinter().print(" ");
        getPrinter().print(node.getName());
        getPrinter().print(" ");
        getPrinter().print("{");
        getPrinter().indent();
        List<ASTUMLPElement> elements = node.getUMLPElementList();
        for (ASTUMLPElement element : elements) {
            element.accept(getTraverser());
            getPrinter().println();
        }
        getPrinter().unindent();
        getPrinter().print("}");
    }


    @Override
    public void handle(ASTUMLPCDElement node) {
        if (node.isPresentCDElement()) {
            node.getCDElement().accept(getTraverser());
        } else{
            node.getCDTargetImportStatement().accept(getTraverser());
        }
    }

    @Override
    public void handle(ASTUMLPCompilationUnit node) {
        if(node.isPresentMCPackageDeclaration()){
            node.getMCPackageDeclaration().accept(getTraverser());
        }
        List<ASTMCImportStatement> importStatements = node.getMCImportStatementList();
        for (ASTMCImportStatement importStatement : importStatements){
            importStatement.accept(getTraverser());
            getPrinter().println();
        }
        node.getUMLPDef().accept(getTraverser());
    }
}

