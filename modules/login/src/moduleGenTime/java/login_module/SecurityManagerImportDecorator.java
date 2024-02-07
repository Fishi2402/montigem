/* (c) https://github.com/MontiCore/monticore */

package login_module;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisHandler;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.guidsl._visitor.GUIDSLVisitor2;
import umlp.UMLPMill;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.Stack;

// Add the imports to the SecManager
public class SecurityManagerImportDecorator  {

  public SecurityManagerImportDecorator(GlobalExtensionManagement glex) {
  }

  @SuppressWarnings("unused")
  public void transform(de.monticore.guidsl._ast.ASTGUICompilationUnit origGui, ASTCDCompilationUnit decoratedCD) {

    GUIDSLTraverser traverser = GUIDSLMill.traverser();

    // "Fun" fact: There exists no symbol table, thus we have to "resolve" on our own
    CDBasisTraverser cdBasisTraverser = UMLPMill.traverser();
    ClassSearcher classSearcher = new ClassSearcher();
    cdBasisTraverser.setCDBasisHandler(classSearcher);

    traverser.add4GUIDSL(new GUIDSLVisitor2() {
      @Override
      public void visit(ASTGUIComponentDeclaration node) {
        if (!node.isPage()) return;

        // Search for a ${Page}Component class
        classSearcher.setSearch(origGui.getPackage(), node.getName() + "Component");

        String fqn = String.join(".", origGui.getPackage(), node.getName() + "Component");
        decoratedCD.accept(cdBasisTraverser);

        if (classSearcher.candidate != null) {
          // And if found: Add the Import
          String secManager = String.join(".", getPackageName(), "login.LoginSecManager");
          CD4C.getInstance().addImport(classSearcher.candidate, secManager);
          // not finding a class might be caused by other modules/TOP
        }
      }
    });

    try {

      origGui.accept(traverser);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  // Resolve in ASTs for a FQN class (if no ST is present)
  protected static class ClassSearcher implements CDBasisHandler {

    CDBasisTraverser t;
    protected String searchPackageName;
    protected String searchClassName;
    protected ASTCDClass candidate;
    @Override
    public CDBasisTraverser getTraverser() {
      return t;
    }

    @Override
    public void setTraverser(CDBasisTraverser traverser) {
      this.t  = traverser;
    }

    Stack<String> packageStack = new Stack<>();

    @Override
    public void handle(ASTCDPackage node) {
      if (alreadyFound) return;

      packageStack.push(node.getName());
      String fqn = String.join(".", packageStack);

      if (searchPackageName.startsWith(fqn))
        CDBasisHandler.super.handle(node);

      packageStack.pop();

    }

    protected boolean alreadyFound = false;
    @Override
    public void handle(ASTCDClass node) {
      if (node.getName().equals(searchClassName)) {
        alreadyFound = true;
        candidate = node;
      }
    }

    public void setSearch(String aPackage, String s) {
      this.searchPackageName = aPackage;
      this.searchClassName  = s;
      this.alreadyFound = false;
      this.candidate = null;
    }
  }

  protected String getPackageName() {
    String fqn = GUIGeneratorUtil.getInstance().getMainDomainModelQName().get();
    return fqn.toLowerCase();
  }

}
