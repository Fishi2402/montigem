/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.domain;

import de.monticore.cd4codebasis._ast.ASTCDMethodSignature;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.StringTransformations;
import umlp.generator.domain.decorator.CommandObserverDecorator;
import umlp.generator.domain.decorator.DAODecorator;
import umlp.generator.domain.decorator.GenService;
import umlp.generator.domain.decorator.ObserverDecorator;
import umlp.generator.gui.gentypescript.decorator.TSImportHelper;
import umlp.generator.util.PathUtil;
import umlp.generator.util.UMLPSymbolTables;

import java.nio.file.Path;

import static umlp.generator.domain.decorator.commands.AssocCommandDecorator.COMMAND_ASSOC_PACKAGE;

public class TSCommandObserverDecorator extends CommandObserverDecorator {

  protected final Path outPath;
  protected final MCPath hwPath;

  public TSCommandObserverDecorator(GlobalExtensionManagement glex, MCPath hwPath, Path outPath) {
    super(glex);
    this.hwPath = hwPath;
    this.outPath = outPath;
  }

  @Override
  protected ASTCDClass createEmptyClass(ASTCDClass domainClass, String classname) {
    ASTCDClass emptyClass = super.createEmptyClass(domainClass, classname);

    // replace interface via mixin
    glex.replaceTemplate("umlp.gui2ts.TSClassImplements",
        emptyClass, new TemplateHookPoint("umlp.gui2ts.TSClassExtendsMixin"));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint("Object"));

    glex.addBeforeTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint(
            domainClass.getName() + ObserverDecorator.SUFFIX + TSObserverMixinDecorator.SUFFIX + "("
        ));
    glex.addAfterTemplate("umlp.gui2ts.TSEmptyMixin",
        emptyClass, new StringHookPoint(")"));

    return emptyClass;
  }

  // override does nothing other than changing import calls (including addImports)
  @Override
  protected ASTCDClass createClass(ASTCDCompilationUnit origCD, ASTCDClass domainClass) {
    String classname = domainClass.getName() + getSuffix();

    ASTCDClass ast = createEmptyClass(domainClass, classname);

    getCD4C().addMethod(ast, scopeTmplt());

    String astcdPackage = getPackageName(origCD, CLIENT_PACKAGE);
    TSImportHelper importHelper = new TSImportHelper(glex, ast, hwPath, outPath);
    String importerQName = astcdPackage + "." + ast.getName();

    // Attribute
    UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).entrySet().stream()
        .filter(e -> !e.getKey().getModifier().isDerived())
        .forEach(e -> {
          ASTCDAttribute a = e.getKey();
          ASTCDType t = e.getValue();
          if (GenService.isList(a)) {
            ASTCDMethodSignature add = getCD4C().addMethod(ast, addAttributeTmplt(),
                t.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                a.getName(), false, false, t.getName());
            ASTCDMethodSignature remove = getCD4C().addMethod(ast, removeAttributeTmplt(),
                t.getName(), a.getName(), GenService.getInnerType(a.getMCType()),
                a.getName(), false, false, t.getName());

            setOverrideAnnotation(add);
            setOverrideAnnotation(remove);

            importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE)+"."+
                t.getName() + "_add" + StringTransformations.capitalize(a.getName()), importerQName);
            importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE)+"."+
                t.getName() + "_remove" + StringTransformations.capitalize(a.getName()), importerQName);
          } else {
            ASTCDMethodSignature set = getCD4C().addMethod(ast, setAttributeTmplt(),
                t.getName(), a.getName(), GenService.createType(a).printType(),
                a.getName(), false, domainClass.getName(), false);

            setOverrideAnnotation(set);

            importHelper.addImport(getPackageName(origCD, COMMAND_PACKAGE)+"."+
                domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()), importerQName);
          }
          if (GenService.isCDEnum(a.getSymbol().getType())) {
            importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+"."+
                a.getSymbol().getType().print(), importerQName);
          }
          if (GenService.isInnerCDEnum(a.getSymbol().getType())) {
            importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE)+"."+
                (((SymTypeOfGenerics) a.getSymbol().getType()).getArgument(0)).print(), importerQName);
          }
        });

    // Assoziationen
    DAODecorator.getOppositeSideToAssociationMap(domainClass).entrySet().stream()
        // TODO @CBU, @AHe how to handle association inherited from superclass from imported CD?
        .filter(e -> UMLPSymbolTables.getDiagramFor(e.getKey()).getCDDefinition().getName().equals(origCD.getCDDefinition().getName()))
        .filter(e -> !(e.getValue().getModifier().isDerived() || e.getValue().getModifier().isReadonly()))
        .forEach(e -> {
          ASTCDAssocSide a = e.getKey();
          ASTCDCompilationUnit cd = UMLPSymbolTables.getDiagramFor(a);

          boolean isBidirectional = GenService.isBidirectional(a);
          boolean isLeft = a.getSymbol().isIsLeft();
          String assocName = a.getSymbol().getAssoc().getAssociation().getName();
          SymTypeExpression oneType = e.getKey().getSymbol().getType();
          SymTypeExpression otherType = e.getKey().getSymbol().getOtherSide().getType();
          if (GenService.isList(a)) {
            ASTCDMethodSignature add = getCD4C().addMethod(ast, addAttributeTmplt(), oneType.print(),
                a.getName(), "long", assocName, isBidirectional, isLeft, otherType.print());
            ASTCDMethodSignature remove = getCD4C().addMethod(ast, removeAttributeTmplt(), oneType.print(),
                a.getName(), "long", assocName, isBidirectional, isLeft, otherType.print());

            setOverrideAnnotation(add);
            setOverrideAnnotation(remove);

            if (isBidirectional) {
              importHelper.addImport(getPackageName(cd, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_add", importerQName);
              importHelper.addImport(getPackageName(cd, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_remove", importerQName);
            } else {
              importHelper.addImport(getPackageName(cd, COMMAND_PACKAGE) + "." +
                  otherType.print() + "_add" + StringTransformations.capitalize(a.getName()), importerQName);
              importHelper.addImport(getPackageName(cd, COMMAND_PACKAGE) + "." +
                  otherType.print() + "_remove" + StringTransformations.capitalize(a.getName()), importerQName);
            }
          } else {
            ASTCDMethodSignature set = getCD4C().addMethod(ast, setAttributeTmplt(), otherType.print(),
                a.getName(), "long", assocName, isBidirectional, domainClass.getName(), isLeft);
            setOverrideAnnotation(set);

            if (isBidirectional) {
              importHelper.addImport(getPackageName(cd, COMMAND_ASSOC_PACKAGE) + "." +
                  StringTransformations.capitalize(assocName) + "_set", importerQName);
            } else {
              importHelper.addImport(getPackageName(cd, COMMAND_PACKAGE) + "." +
                  domainClass.getName() + "_set" + StringTransformations.capitalize(a.getName()), importerQName);
            }
          }

          ASTCDCompilationUnit cdOne = UMLPSymbolTables.getDiagramFor((ASTCDType) oneType.getTypeInfo().getAstNode());
          ASTCDCompilationUnit cdOther = UMLPSymbolTables.getDiagramFor((ASTCDType) otherType.getTypeInfo().getAstNode());
          importHelper.addImport(getPackageName(cdOne, DEFAULT_PACKAGE) + "." + oneType.print(), importerQName);
          importHelper.addImport(getPackageName(cdOther, DEFAULT_PACKAGE) + "." + otherType.print(), importerQName);
        });
    addImports(UMLPSymbolTables.getDiagramFor(domainClass), domainClass, ast, importHelper, importerQName);

    return ast;
  }

  @Override
  protected void setOverrideAnnotation(ASTCDMethodSignature m) {
    // don't
  }

  protected void addImports(ASTCDCompilationUnit origCD, ASTCDType domainClass, ASTCDType clazz,
      TSImportHelper importHelper, String importerQName
  ) {
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommonPath("NotificationScope"));
    getCD4C().addImport(clazz, PathUtil.getBackendRTECommandPath("CommandManager"));

    for (ASTCDType t : UMLPSymbolTables.getAttributesInHierarchyWithDefiningType(domainClass).values()) {
      ASTCDCompilationUnit def = UMLPSymbolTables.getDiagramFor(t);
      importHelper.addImport(getPackageName(def, DEFAULT_PACKAGE) + "." + t.getName(), importerQName);
    }

    String obs = domainClass.getName() + ObserverDecorator.SUFFIX + TSObserverMixinDecorator.SUFFIX;
    importHelper.addImport(getPackageName(origCD, DEFAULT_PACKAGE) + "." + obs, importerQName);
  }

}
