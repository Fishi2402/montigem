/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.genhtml;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.monticore.guidsl._ast.ASTGUIGuard;
import de.monticore.guidsl._ast.ASTGUIIterate;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GUI2HtmlTrafo implements GUIDSLVisitor2 {

  protected static final GUIGeneratorUtil util = GUIGeneratorUtil.getInstance();
  protected GUIDSLTraverser traverser;

  // For HTML printing only. No symbol table to be constructed
  protected ASTGUICompilationUnit out;
  protected ASTGUICompilationUnit in;

  protected Set<ASTGUIBlockContent> templates = new HashSet<>();

  protected Optional<ASTGUIComponent> current = Optional.empty();

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
    out.accept(getTraverser());
    if (out.getGUIComponentDeclaration().isPresentGUIBlock()) {
      out.getGUIComponentDeclaration().getGUIBlock().addAllGUIBlockContents(templates);
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
    else {
      String templateId = util.getNgTemplateName(node.getId().getName());
      // TODO AGe: better solution, setting the name here makes derivation of method names incorrect
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIComponent clone = node.deepClone();
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIComponent(clone)
          .build();
      templates.add(content);
    }
  }

  @Override
  public void endVisit(ASTGUIGuard node) {
    if (current.isPresent()) {
      String templateId = util.getNgTemplateName(node.getId().getName());
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIGuard clone = node.deepClone();
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIGuard(clone)
          .build();
      templates.add(content);
    }
  }

  @Override
  public void endVisit(ASTGUIIterate node) {
    if (current.isPresent()) {
      String templateId = util.getNgTemplateName(node.getId().getName());
      node.getId().setName(util.getNgTemplateActualName(templateId));
      ASTGUIIterate clone = node.deepClone();
      clone.getId().setName(templateId);
      ASTGUIBlockContent content = GUIDSLMill.gUIBlockContentBuilder()
          .setGUIIterate(clone)
          .build();
      templates.add(content);
    }
  }

}
