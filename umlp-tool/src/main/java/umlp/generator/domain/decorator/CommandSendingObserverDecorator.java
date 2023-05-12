/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain.decorator;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.StringTransformations;
import umlp.UMLPMill;
import umlp.generator.AbstractDecorator;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static umlp.generator.domain.decorator.commands.AssocCommandDecorator.COMMAND_ASSOC_PACKAGE;

public class CommandSendingObserverDecorator extends AbstractDecorator {

  public static final String SUFFIX = "CommandSendingObserver";

  protected String getSuffix(){
    return SUFFIX;
  }

  public CommandSendingObserverDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public void transform(ASTCDCompilationUnit origCD, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage astcdPackage = getPackage(origCD, decoratedCD, CLIENT_PACKAGE);
    origCD.getCDDefinition().getCDClassesList().stream()
        .filter(c -> !c.getModifier().isDerived())
        .filter(c -> !c.getModifier().isAbstract())
        .forEach(c -> astcdPackage.addCDElement(createClass(origCD, c)));
  }

  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String classname = domainClass.getName() + getSuffix();

    ASTCDClass ast = UMLPMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setCDInterfaceUsage(getInterfaceUsageFacade().createCDInterfaceUsage(domainClass.getName()+"Observer"))
            .setName(classname)
            .build();

    getCD4C().addMethod(ast, scopeTmplt());

    // Attribute
    UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).entrySet().stream()
            .filter(e -> !e.getKey().getModifier().isDerived())
            .forEach(e -> {
              ASTCDAttribute a = e.getKey();
              ASTCDType t = e.getValue();
              if (GenService.isList(a)) {
                ASTCDMethodSignature add = getCD4C().addMethod(ast, addAttributeTmplt(),
                    t.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                    a.getName(), false, t.getName());
                ASTCDMethodSignature remove = getCD4C().addMethod(ast, removeAttributeTmplt(),
                    t.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                    a.getName(), false, t.getName());

                setOverrideAnnotation(add);
                setOverrideAnnotation(remove);

                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                    t.getName() + "_add" + StringTransformations.capitalize(a.getName()));
                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                    t.getName() + "_remove" + StringTransformations.capitalize(a.getName()));
              } else {
                ASTCDMethodSignature set = getCD4C().addMethod(ast, setAttributeTmplt(),
                    t.getName(), a.getName(), GenService.createType(a).printType(),
                    a.getName(), false, domainClass.getName());

                setOverrideAnnotation(set);

                getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE)+"."+
                    domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()));
              }
              if (GenService.isCDEnum(a.getSymbol().getType())) {
                getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE)+"."+
                        a.getSymbol().getType().print());
              }
            });

    // Assoziationen
    DAODecorator.getOppositeSideToAssociationMap(domainClass, origCD.getCDDefinition().getCDAssociationsList()).entrySet().stream()
        .filter(e -> !(e.getValue().getModifier().isDerived() || e.getValue().getModifier().isReadonly()))
        .forEach(e -> {
          ASTCDAssocSide a = e.getKey();
          boolean isBidirectional = GenService.isBidirectional(a);
          String assocName = a.getSymbol().getAssoc().getAssociation().getName();
          SymTypeExpression oneType = e.getKey().getSymbol().getType();
          SymTypeExpression otherType = e.getKey().getSymbol().getOtherSide().getType();
          if (GenService.isList(a)) {
            ASTCDMethodSignature add = getCD4C().addMethod(ast, addAttributeTmplt(), oneType.print(),
                a.getName(), "long", assocName, isBidirectional, otherType.print());
            ASTCDMethodSignature remove = getCD4C().addMethod(ast, removeAttributeTmplt(), oneType.print(),
                a.getName(), "long", assocName, isBidirectional, otherType.print());

            setOverrideAnnotation(add);
            setOverrideAnnotation(remove);

            if (isBidirectional) {
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_add");
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_remove");
            } else {
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                  otherType.print() + "_add" + StringTransformations.capitalize(a.getName()));
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                  otherType.print() + "_remove" + StringTransformations.capitalize(a.getName()));
            }
          } else {
            ASTCDMethodSignature set = getCD4C().addMethod(ast, setAttributeTmplt(), otherType.print(),
                a.getName(), "long", assocName, isBidirectional, domainClass.getName());
            setOverrideAnnotation(set);

            if (isBidirectional) {
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_set");
            } else {
              getCD4C().addImport(ast, getPackageName(origCD, COMMAND_PACKAGE) + "." +
                  domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()));
            }
          }

          getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + otherType.print());
          getCD4C().addImport(ast, getPackageName(origCD, DEFAULT_PACKAGE) + "." + oneType.print());
        });
    addImports(origCD, domainClass, ast);

    return ast;
  }

  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    glex.replaceTemplate("cd2java.Annotations", m, new StringHookPoint("@Override"));
  }

  protected String setAttributeTmplt() {
    return "umlp.commandsendingobserver.NotifySetAttribute";
  }

  protected String removeAttributeTmplt() {
    return "umlp.commandsendingobserver.NotifyRemoveAttribute";
  }

  protected String addAttributeTmplt() {
    return "umlp.commandsendingobserver.NotifyAddAttribute";
  }

  protected String scopeTmplt() {
    return "umlp.commandobserver.GetScope";
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainClass, ASTCDType clazz) {
    super.addImports(domainClass, clazz);
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));

    UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).values().stream()
        .map(ASTCDType::getName).distinct()
        .forEach(t -> getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+"."+ t));
    getCD4C().addImport(clazz, getPackageName(origCD, DEFAULT_PACKAGE)+"."+domainClass.getName()+ObserverDecorator.SUFFIX);
  }

}
