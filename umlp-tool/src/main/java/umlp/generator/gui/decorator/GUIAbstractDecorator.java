package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import umlp.generator.AbstractDecorator;

import java.util.Locale;

public abstract class GUIAbstractDecorator extends AbstractDecorator {

  public static MCBasicTypesFullPrettyPrinter basicTypesPrinter;

  public static final String COMPONENT_SUFFIX = "GUI";

  public GUIAbstractDecorator(final GlobalExtensionManagement glex) {
    super(glex);
    basicTypesPrinter = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
  }

  public abstract void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST);

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
  }

  public static String getComponentClassName(ASTGUIComponentDeclaration guiAst) {
    return guiAst.getName() + COMPONENT_SUFFIX;
  }

  public static String getPackageName(ASTGUICompilationUnit guiAst, String subPackage) {
    String origPackage = Names.constructQualifiedName(guiAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList(),
        guiAst.getComponentName().toLowerCase(Locale.ROOT));
    return (subPackage.isEmpty()?origPackage: Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

}
