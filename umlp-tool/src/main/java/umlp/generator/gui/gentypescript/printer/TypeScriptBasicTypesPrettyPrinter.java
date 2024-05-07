package umlp.generator.gui.gentypescript.printer;

import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.List;

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
      // TODO AGe: underspecified List lands here
      if (node.getBaseName().equals("E")) {
        getPrinter().print("any");
      }
      // TODO AGe: underspecified List landed here, but not anymore.
      //  Wasn't pretty in the first place.
      else if (node.getBaseName().equals(TypeScriptTypes.LIST.getMCType())) {
        getPrinter().print(TypeScriptTypes.LIST.getTSType() + "<any>");
      }
      else if (node.getBaseName().equals(TypeScriptTypes.EVENT.getMCType())) {
        getPrinter().print(TypeScriptTypes.EVENT.getTSType() + "<any>");
      }
      else {
        getPrinter().print(TypeScriptTypes.toTSType(node.getBaseName()));
      }
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
    if (node.getMCQualifiedName().getBaseName().equals("E")) {
      return;
    }
    if (node.getQName().equals(BuiltInTypes.EVENT.getQName())) {
      getPrinter().print("import { " + TypeScriptTypes.EVENT.getTSType()
          + " } from '@angular/core';");
    }
    else {
      getPrinter().print("import { ");
      node.getMCQualifiedName().accept(getTraverser());
      getPrinter().print(" }");
      // TODO: star import?
      getPrinter().stripTrailing();
      getPrinter().print(" from '");

      // --- path calculation (start)
      // TODO AGe: generated path doesn't match package for CDs
      //  hardy.Flensburg.Human -> hardy/flensburg/Human
      List<String> parts = node.getMCQualifiedName().getPartsList();
      int index = parts.size() - 2;
      if (index >= 0) {
        parts.set(index, parts.get(index).toLowerCase());
      }
      String from = "@src/" + util.qName2path(String.join(".", parts));
      for (TSImportMap type : TSImportMap.values()) {
        if (node.getQName().startsWith(type.getJavaPackage())) {
          from = type.getTSPackage();
        }
      }
      if (TSBackendMap.isBackendRTEPackage(node.getQName())) {
        from = TSBackendMap.getTSRTEPackage();
      }
      getPrinter().print(from);
      // --- path calculation (end)

      getPrinter().println("';");
    }
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
