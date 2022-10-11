package umlp.generator.gui.decorator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponentDeclaration;
import de.monticore.guidsl._ast.ASTGUIParam;
import de.monticore.guidsl.prettyprint.GUIDSLFullPrettyPrinter;
import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.TypeCalculator;
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
    // --- replace this when TypeCheck can be used

  //  String domainTypeName = astguiParam.getMCType().printType(basicTypesPrinter);  // TODO GV
    String attributeType = new GUIDSLFullPrettyPrinter().prettyprint(astguiParam.getMCType());

    TypeSymbol typeInfo = astguiParam.getSymbol().getType().getTypeInfo();
    TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL());

    boolean isDomainType = !astguiParam.getSymbol().getType().isGenericType();

    // TODO when ST ist fertig: qualified domainTypeName und add import declaration
    String qualifiedType = "hardy." + typeInfo.getName(); // domainTypeSymbol.getQualifiedName(); //TODO MB

    // --- end

    getCD4C().addImport(clazz, qualifiedType);

    ASTCDAttribute domainAttribute = getCDAttributeFacade().createAttribute(CDModifier.PROTECTED.build(),
        attributeType, astguiParam.getName());
    clazz.addCDMember(domainAttribute);

    // TODO GV
    if (isDomainType) {
      clazz.setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(typeInfo.getName() + ERROR_OBSERVER));
      getCD4C().addImport(clazz, qualifiedType + ERROR_OBSERVER);
    }

    getCD4C().addConstructor(clazz, "umlp.gui2java.Constructor", clazz.getName(), attributeType, astguiParam.getName(), isDomainType);

    getCD4C().addMethod(clazz, "umlp.gui2java.Init");
  }

}
