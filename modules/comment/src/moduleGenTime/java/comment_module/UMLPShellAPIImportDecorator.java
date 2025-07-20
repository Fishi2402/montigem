package comment_module;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp._auxiliary.CDBasisMillForUMLP;
import umlp.generator.AbstractDecorator;

@SuppressWarnings("unused")
public class UMLPShellAPIImportDecorator extends AbstractDecorator {
  
  public UMLPShellAPIImportDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }
  
  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, "shellapi");
    
    CDBasisTraverser traverser = CDBasisMillForUMLP.inheritanceTraverser();
    traverser.add4CDBasis(new ShellAPIClassVisitor());
    astcdPackage.accept(traverser);
  }
  
  protected static class ShellAPIClassVisitor implements CDBasisVisitor2 {
    
    @Override
    public void endVisit(ASTCDClass cdClass) {
      getCD4C().addImport(cdClass, "commentmodule.CommentModuleManager");
      getCD4C().addImport(cdClass, "commentmodule.CommentModuleNote");
      getCD4C().addImport(cdClass, "commentmodule.CommentModuleVisibility");
    }
  }
}
