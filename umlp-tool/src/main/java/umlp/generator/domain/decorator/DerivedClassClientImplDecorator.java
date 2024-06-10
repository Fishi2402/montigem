/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

/**
 * The generated ClientImpl sends requests to the backend
 */
public class DerivedClassClientImplDecorator extends AbstractDecorator {

  public DerivedClassClientImplDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    getDerivedDomainClasses(origCD).forEach(c -> astcdPackage.addCDElement(withOrigin(createClass(c,
                                                                                                  origCD))));
  }

  protected ASTCDClass createClass(ASTCDClass domainClass, ASTCDCompilationUnit origCD) {
    String classname = domainClass.getName() + "ClientImpl";

    ASTCDClass ast = UMLPMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
      .setName(classname).build();


    String packageName = getPackageName(origCD, DEFAULT_PACKAGE);

    addConstructor(ast, classname);

    if (!ast.isPresentCDExtendUsage())
      ast.setCDExtendUsage(UMLPMill.cDExtendUsageBuilder().build());

    ast.getSuperclassList().add(getMCTypeFacade().createQualifiedType(domainClass.getName()));

    // For each attribute
    List<ASTCDAttribute> attributeList =
    CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
      .filter(a -> !a.getModifier().isDerived())
        .collect(Collectors.toList());
    attributeList.forEach(a -> handleAttribute(domainClass, a.getName(), a.getSymbol().getType().print(), ast, packageName, false));

    List<ASTCDAssocSide> singleAssocs = new ArrayList<>();
    List<ASTCDAssocSide> listAssocs = new ArrayList<>();

    // For each association
    CDSymbolTables.getAssociationsInHierarchy(domainClass)
        .forEach(assoc -> {
          String type = assoc.getSymbol().getType().print();
          boolean isList = GenService.isList(assoc);
          if (isList) {
            type = toList(type);
            listAssocs.add(assoc);
          } else {
            singleAssocs.add(assoc);
          }
          handleAttribute(domainClass, assoc.getName(), type, ast, packageName, isList);
        });

    // Add the init template (which might execute the commands)
    getCD4C().addMethod(ast, getInitTemplate(), domainClass.getName(), attributeList, singleAssocs, listAssocs);


    addImports(domainClass, ast);

    return ast;
  }

  protected void addConstructor(ASTCDClass ast, String classname) {
    getCD4C().addConstructor(ast, getConstructorTemplate(), classname);
  }

  protected String toList(String type) {
    return "List<" + type + ">";
  }

  protected void handleAttribute(ASTCDClass domainClass, String name, String type, ASTCDClass ast,
      String packageName, boolean isList) {
    ASTCDMethodSignature calc = getCD4C().addMethod(ast, getCalcTemplate(),
        name, type, domainClass.getName(), packageName, isList);
    getCD4C().addMethod(ast, getGetTemplate(), name, type, calc);
    if (isList) {
      ASTCDMethodSignature calcSize = getCD4C().addMethod(ast, getCalcSizeTemplate(),
          name, type, domainClass.getName(), packageName);
      getCD4C().addMethod(ast, getSizeTemplate(), name, type, calcSize);
    }
  }

  protected String getConstructorTemplate() {
    return "umlp.derivedclass.Constructor";
  }

  protected String getGetTemplate() {
    return "umlp.derivedclass.GetClientImpl";
  }

  protected String getSizeTemplate() {
    return "umlp.derivedclass.GetSizeImpl";
  }

  protected String getInitTemplate() {
    return "umlp.derivedclass.Init";
  }

  protected String getCalcTemplate() {
    return "umlp.derivedclass.CalcClientImpl";
  }

  protected String getCalcSizeTemplate() {
    return "umlp.derivedclass.CalcSizeImpl";
  }

  @Override
  protected void addImports(ASTCDType domainType, ASTCDType cdType) {
    getCD4C().addImport(cdType, PathUtil.getBackendRTECommandPath("CommandManager"));
  }
}
