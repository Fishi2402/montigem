package de.monticore.dsl2cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cd4codebasis._visitor.CD4CodeBasisVisitor2;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.ArrayList;
import java.util.List;

public class ClassSearcher {
  public static List<ASTCDInterface> lookForTraversers(List<ASTCDCompilationUnit> cds) {
    var t = CD4CodeMill.inheritanceTraverser();
    List<ASTCDInterface> traversers = new ArrayList<>();
    t.add4CD4CodeBasis(new CD4CodeBasisVisitor2() {
      @Override
      public void visit(ASTCDInterface node) {
        // package de.monticore.expressions.javaclassexpressions._visitor
        // public interface JavaClassExpressionsTraverser
        if (node.getName().endsWith("Traverser") && node.getSymbol().getFullName().contains("._visitor.")) {
          traversers.add(node);
        }
      }
    });

    cds.forEach(astcdCompilationUnit -> astcdCompilationUnit.accept(t));

    return traversers;
  }
  public static List<ASTCDInterface> lookForHandlers(List<ASTCDCompilationUnit> cds) {
    var t = CD4CodeMill.inheritanceTraverser();
    List<ASTCDInterface> handlers = new ArrayList<>();
    t.add4CD4CodeBasis(new CD4CodeBasisVisitor2() {
      @Override
      public void visit(ASTCDInterface node) {
        // public interface JavaClassExpressionsHandler extends de.monticore.visitor.IHandler
        if (!node.isPresentCDExtendUsage()) return;
        if (node.getCDExtendUsage().getSuperclass(0).printType().equals("de.monticore.visitor.IHandler")) {
          handlers.add(node);
        }
      }
    });

    cds.forEach(astcdCompilationUnit -> astcdCompilationUnit.accept(t));

    return handlers;
  }

  public static List<String> lookForPackagesOfMills(ASTCDCompilationUnit dslCD) {
    var t = CD4CodeMill.inheritanceTraverser();
    List<String> handlers = new ArrayList<>();
    t.add4CD4CodeBasis(new CD4CodeBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (!node.isPresentCDExtendUsage()) return;
        if (node.getName().contains("Mill")) {
          String type = node.getCDExtendUsage().getSuperclass(0).printType();
          type = type.substring(0, type.lastIndexOf("."));
          handlers.add(type);
        }
      }
    });
    dslCD.getCDDefinition().getCDPackagesList().stream().filter(p->p.getName().endsWith("._auxiliary")).forEach(
            p->p.accept(t)
                                                                                                               );


    return handlers;
  }
}
