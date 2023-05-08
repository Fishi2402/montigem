package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import umlp.generator.gui.GUIGeneratorUtil;

public class TypeScriptBasicTypesPrettyPrinter extends MCBasicTypesPrettyPrinter {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();

  public TypeScriptBasicTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  public void handle(de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    // print only last
    if (node.getPartsList().size() > 0) {
      getPrinter().print(TypeScriptTypes.toTSType(node.getBaseName()));
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.types.mcbasictypes._ast.ASTMCImportStatement node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("import {");
    node.getMCQualifiedName().accept(getTraverser());
    getPrinter().print("}");
    // TODO: star import?
    getPrinter().stripTrailing();
    getPrinter().print(" from '");
    getPrinter().print(util.java2tsPackage(node.getQName()));
    getPrinter().println("';");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.BOOLEAN) {
      getPrinter().print("boolean ");
    }
    else if (node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.BYTE
        || node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.SHORT
        || node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.INT
        || node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.LONG
        || node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.FLOAT
        || node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.DOUBLE
    ) {
      getPrinter().print("number ");
    }
    else if (node.getPrimitive() == de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes.CHAR) {
      getPrinter().print("string ");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}