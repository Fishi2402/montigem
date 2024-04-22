package umlp.generator.gui.gentypescript.printer;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.List;
import java.util.Optional;

public class TypeScriptBasicTypesPrettyPrinter extends MCBasicTypesPrettyPrinter {

  public TypeScriptBasicTypesPrettyPrinter(IndentPrinter printer) {
    super(printer, false);
  }

  @Override
  public void handle(ASTMCQualifiedName node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    // print only last
    if (!node.getPartsList().isEmpty()) {
      getPrinter().print(TypeScriptTypes.toTSType(node.getBaseName()));
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTMCImportStatement node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    Optional<String> tsImportPath = TypeScriptTypes.getTsImportPath(node.getQName());
    if (tsImportPath.isPresent()) {
      getPrinter().print("import { " + TypeScriptTypes.toTSType(node.getQName())
          + " } from '" + tsImportPath.get() + "';");
    }
    else if (!TypeScriptTypes.isDefault(node.getQName())) {
      getPrinter().print("import { ");
      node.getMCQualifiedName().accept(getTraverser());
      getPrinter().print(" }");
      getPrinter().stripTrailing();
      getPrinter().print(" from '");

      // generated path doesn't match package for domain classes
      // hardy.Flensburg.Human -> hardy/flensburg/Human
      List<String> parts = node.getMCQualifiedName().getPartsList();
      int index = parts.size() - 2;
      if (index >= 0) {
        parts.set(index, parts.get(index).toLowerCase());
      }
      String from = "@src/" + GUIGeneratorUtil.getInstance().qName2path(String.join(".", parts));
      tsImportPath = TSImportMap.getTSPackage(node.getQName());
      if (tsImportPath.isPresent()) {
        from = tsImportPath.get();
      }
      getPrinter().print(from);

      getPrinter().println("';");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTMCPrimitiveType node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.getPrimitive() == ASTConstantsMCBasicTypes.BOOLEAN) {
      getPrinter().print("boolean ");
    }
    else if (node.getPrimitive() == ASTConstantsMCBasicTypes.BYTE
        || node.getPrimitive() == ASTConstantsMCBasicTypes.SHORT
        || node.getPrimitive() == ASTConstantsMCBasicTypes.INT
        || node.getPrimitive() == ASTConstantsMCBasicTypes.LONG
        || node.getPrimitive() == ASTConstantsMCBasicTypes.FLOAT
        || node.getPrimitive() == ASTConstantsMCBasicTypes.DOUBLE
    ) {
      getPrinter().print("number ");
    }
    else if (node.getPrimitive() == ASTConstantsMCBasicTypes.CHAR) {
      getPrinter().print("string ");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
