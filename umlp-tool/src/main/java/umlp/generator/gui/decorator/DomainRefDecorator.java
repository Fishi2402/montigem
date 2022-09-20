package umlp.generator.gui.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import umlp.UMLPMill;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DomainRefDecorator extends GUIAbstractDecorator {

  public static final String ERROR_OBSERVER = "ErrorObserver";

  public DomainRefDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTGUICompilationUnit guiAst, ASTCDCompilationUnit decoratedAST) {
    String packageName = getPackageName(guiAst, DEFAULT_PACKAGE);
    ASTCDPackage astcdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(getMCTypeFacade().createQualifiedName(packageName)).build();
    decoratedAST.getCDDefinition().addCDElement(astcdPackage);

    ASTCDClass guiClass = createClass(guiAst.getGUIComponentDeclaration(),
        guiAst.getGUIComponentDeclaration().getName());
    astcdPackage.addCDElement(guiClass);
  }

  protected ASTCDClass createClass(ASTGUIComponentDeclaration domainClass, String name) {
    ASTCDClassBuilder builder = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(getComponentClassName(domainClass));
    ASTCDClass clazz = builder.build();

    if (!domainClass.getGUIParams().getGUIParamList().isEmpty()) {
      addDomainMembers(clazz, domainClass.getGUIParams().getGUIParamList().get(0));
    }
    else {
      getCD4C().addConstructor(clazz, "umlp.domain.Constructor", domainClass.getName());
    }

    return clazz;
  }

  protected void addDomainMembers(ASTCDClass clazz, ASTGUIParam astguiParam) {
    String domainTypeName = astguiParam.getMCType().printType(basicTypesPrinter);  // TODO GV
    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();
    //   TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());
    // TODO when ST ist fertig: resove domainTypeName und add import declaration //MB
    String qualifiedType = "hardy." + domainTypeName; // domainTypeSymbol.getQualifiedName();
    getCD4C().addImport(clazz, qualifiedType);

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        domainTypeName, astguiParam.getName());
    clazz.addCDMember(domainAttribute);

    // TODO GV
    clazz.setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(domainTypeName + ERROR_OBSERVER));
    getCD4C().addImport(clazz, qualifiedType + ERROR_OBSERVER);

    getCD4C().addConstructor(clazz, "umlp.gui2java.Constructor", clazz.getName(), domainTypeName, astguiParam.getName());

  }

}
