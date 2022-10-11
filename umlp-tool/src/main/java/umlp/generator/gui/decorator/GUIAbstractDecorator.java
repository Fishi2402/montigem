package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._symboltable.GUIVariableSymbol;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import umlp.generator.AbstractDecorator;

import java.util.Optional;

public abstract class GUIAbstractDecorator extends AbstractDecorator {

  public static MCBasicTypesFullPrettyPrinter basicTypesPrinter;
  public static GUIDSLFullPrettyPrinter guiDSLPrinter;

  public static final String COMPONENT_SUFFIX = "";

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
    String origPackage = Names.constructQualifiedName(guiAst.getMCPackageDeclaration().getMCQualifiedName().getPartsList());
    return (subPackage.isEmpty() ? origPackage : Joiners.DOT.join(origPackage, subPackage)).toLowerCase();
  }

  // TODO MB
  public Optional<VariableSymbol> resolveAttribute(Optional<GUIVariableSymbol> guiVariableSymbol, String name) {
    if (guiVariableSymbol.isEmpty()) {
      return Optional.empty();
    }
    TypeSymbol typeInfo = guiVariableSymbol.get().getType().getTypeInfo();
    System.err.println(" TypeSymbol " + typeInfo.getName() + " name " + name);
    Optional<VariableSymbol> res = typeInfo.getSpannedScope().resolveVariable(name);
    System.err.println(" VariableSymbol " + (res.isEmpty()? " fehlt " : res.get().getName()+ " type " + res.get().getType()));
    if (res.isEmpty() && typeInfo.isPresentSuperClass()) {
      res = typeInfo.getSuperClass().getTypeInfo().getSpannedScope().resolveVariable(name);
      System.err.println(" VariableSymbol 22 " + (res.isEmpty()? " fehlt " : res.get().getName()+ " type " + res.get().getType()));
    }
    return res;
  }
}
