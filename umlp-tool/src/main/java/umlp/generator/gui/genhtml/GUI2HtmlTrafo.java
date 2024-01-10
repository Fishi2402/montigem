/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genhtml;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIBlock;
import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIDSLNode;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GUI2HtmlTrafo implements GUIDSLVisitor2 {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected GUIDSLTraverser traverser;

  // For HTML printing only. No symbol table to be constructed
  protected ASTGUICompilationUnit out;
  protected ASTGUICompilationUnit in;

  protected Map<ASTGUIBlock, Set<ASTGUIBlockContent>> templates = new LinkedHashMap();
  protected Deque<ASTGUIBlock> holderStack = new ArrayDeque<>();

  protected Optional<ASTGUIComponent> current = Optional.empty();
  protected Optional<ASTGUIGuard> currentGuard = Optional.empty();
  protected Set<ASTGUIDSLNode> guardConditions = new HashSet<>();

  public GUI2HtmlTrafo() {
    traverser = GUIDSLMill.traverser();
    traverser.add4GUIDSL(this);
  }

  public GUIDSLTraverser getTraverser() {
    return traverser;
  }

  public ASTGUICompilationUnit getOut() {
    return out;
  }

  public void transform(ASTGUICompilationUnit ast) {
    in = ast;
    out = ast.deepClone();
    if (out.getGUIComponentDeclaration().isPresentGUIBlock()) {
      holderStack.push(out.getGUIComponentDeclaration().getGUIBlock());
    }
    out.accept(getTraverser());
    if (out.getGUIComponentDeclaration().isPresentGUIBlock()) {
      for (ASTGUIBlock block : templates.keySet()) {
        block.addAllGUIBlockContents(templates.get(block));
      }
    }
  }

  @Override
  public void visit(ASTGUIComponent node) {
    if (current.isEmpty()) {
      current = Optional.of(node);
    }
  }

  @Override
  public void endVisit(ASTGUIComponent node) {
    if (current.isPresent() && current.get().equals(node)) {
      current = Optional.empty();
    }
    else if (currentGuard.isEmpty() || !guardConditions.contains(node)) {
      String templateId = util.getNgTemplateName(node.getId().getName());
      // TODO AGe: better solution, setting the name here makes derivation of method names incorrect
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIComponent clone = node.deepClone();
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIComponent(clone)
          .build();
      addTemplate(content);
    }
  }

  @Override
  public void visit(ASTGUIGuard node) {
    if (currentGuard.isEmpty()) {
      setCurrentGuard(node);
    }
  }

  @Override
  public void endVisit(ASTGUIGuard node) {
    if (current.isPresent() && currentGuard.isPresent() && currentGuard.get().equals(node)) {
      String templateId = util.getNgTemplateName(node.getId().getName());
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIGuard clone = node.deepClone();
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIGuard(clone)
          .build();
      addTemplate(content);
      clearCurrentGuard();
    }
  }

  @Override
  public void visit(ASTGUIIterate node) {
    holderStack.push(node.getGUIBlock());
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    holderStack.pop();
    if (current.isPresent()) {
      String templateId = util.getNgTemplateName(node.getId().getName());
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIIterate clone = node.deepClone();
      if (templates.containsKey(node.getGUIBlock())) {
        templates.put(clone.getGUIBlock(), templates.get(node.getGUIBlock()));
      }
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIIterate(clone)
          .build();
      addTemplate(content);
    }
  }

  protected void addTemplate(ASTGUIBlockContent content) {
    if (!templates.containsKey(holderStack.peek())) {
      templates.put(holderStack.peek(), new HashSet<>());
    }
    templates.get(holderStack.peek()).add(content);
  }

  protected void addCondition(ASTGUIBlockContent block) {
    if (block.isPresentGUIComponent()) {
      guardConditions.add(block.getGUIComponent());
    }
    if (block.isPresentGUIGuard()) {
      guardConditions.add(block.getGUIGuard());
    }
    if (block.isPresentGUIIterate()) {
      guardConditions.add(block.getGUIIterate());
    }
  }

  protected void setCurrentGuard(ASTGUIGuard node) {
    currentGuard = Optional.of(node);
    node.getThenStatement().getGUIBlockContentList().forEach(e -> addCondition(e));
    if (node.isPresentElseStatement()) {
      node.getElseStatement().getGUIBlockContentList().forEach(e -> addCondition(e));
    }
  }

  protected void clearCurrentGuard() {
    currentGuard = Optional.empty();
    guardConditions.clear();
  }

}
