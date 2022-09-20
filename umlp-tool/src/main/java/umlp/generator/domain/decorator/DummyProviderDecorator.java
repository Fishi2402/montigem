/* (c) https://github.com/MontiCore/monticore */

package umlp.generator.domain.decorator;

import com.google.common.collect.Lists;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cd.codegen.methods.MethodDecorator;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class DummyProviderDecorator extends AbstractDecorator {

  public static final String SUFFIX = "DummyProvider";

  public DummyProviderDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  public final void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, DEFAULT_PACKAGE);
    astcdPackage.addCDElement(createClass(origCD));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD) {
    String name = origCD.getCDDefinition().getName();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(name+SUFFIX)
            .build();
    // constructors
    getCD4C().addConstructor(ast, "umlp.dummyprovider.Constructor1", ast.getName());
    getCD4C().addConstructor(ast, "umlp.dummyprovider.Constructor2", ast.getName());

    List<ASTCDClass> classList = origCD.getCDDefinition().getCDClassesList().stream()
            .filter(c -> !c.getModifier().isDerived()).collect(Collectors.toList());

    // attributes
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    attrList.add(getCD4C().addAttribute(ast, "protected int minInt = 0;"));
    attrList.add(getCD4C().addAttribute(ast, "protected int maxInt = 1000;"));
    attrList.add(getCD4C().addAttribute(ast, "protected Random random;"));
    attrList.add(getCD4C().addAttribute(ast, "protected int stringMinLength = 10;"));
    attrList.add(getCD4C().addAttribute(ast, "protected int stringMaxLength = 100;"));
    ASTCDAttribute possChars = getCD4C().addAttribute(ast, "protected char[] possibleCharacters;");
    getGlex().replaceTemplate("cd2java.Value", possChars,
            new TemplateHookPoint("umlp.dummyprovider.PossibleCharacters"));
    attrList.add(possChars);
    classList.stream().forEach(c -> attrList.add(getCD4C().addAttribute(ast,
            "protected int max" + c.getName() + " = 100;")));

    // getter and setter
    MethodDecorator methodDecorator = new MethodDecorator(getGlex());
    attrList.forEach(a -> ast.addAllCDMembers(methodDecorator.decorate(a)));

    // default invent methods
    getCD4C().addMethod(ast, "umlp.dummyprovider.InventInt");
    getCD4C().addMethod(ast, "umlp.dummyprovider.InventIntInRange");
    getCD4C().addMethod(ast, "umlp.dummyprovider.InventString");
    getCD4C().addMethod(ast, "umlp.dummyprovider.InventStringParam");

    // methods
    addInventMethods(classList, ast, origCD.getCDDefinition().getName());

    // Imports
    getCD4C().addImport(ast, "java.util.Random");
    getCD4C().addImport(ast, "com.google.common.collect.Streams");
    getCD4C().addImport(ast, "java.util.Objects");
    getCD4C().addImport(ast, "java.util.stream.Collector");
    getCD4C().addImport(ast, "java.util.stream.IntStream");

    return ast;
  }

  protected void addInventMethods(List<ASTCDClass> domainClassList, ASTCDClass ast, String providerName) {
    for (ASTCDClass domainClass: domainClassList) {
      // Berücksichtige erstmal nur alle (auch Super-)Attribute vom Typ String oder int
      List<ASTCDAttribute> attrList = CDSymbolTables.getAttributesInHierarchy(domainClass).stream()
              .filter(a -> getTypeCheck().isInt(a.getSymbol().getType()) || getTypeCheck().isString(a.getSymbol().getType()))
              .collect(Collectors.toList());
      attrList.forEach(a -> getCD4C().addMethod(ast, "umlp.dummyprovider.InventAttribute", domainClass.getName(), a.getName(), a.printType()));
      getCD4C().addMethod(ast, "umlp.dummyprovider.InventClass",
              domainClass.getName(), providerName, attrList.stream().map(a -> a.getName()).collect(Collectors.toList()));
    }
  }

}
