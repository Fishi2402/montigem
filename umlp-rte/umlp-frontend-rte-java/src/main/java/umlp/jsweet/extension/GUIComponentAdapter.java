package umlp.jsweet.extension;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class GUIComponentAdapter extends PrinterAdapter {

  public static final String COMPONENT_ANNOTATION = "umlp.jsweet.extension.annotation.Component";
  public static final String DIRECTIVE_ANNOTATION = "umlp.jsweet.extension.annotation.Directive";
  public static final String INPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Input";
  public static final String OUTPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Output";
  public static final String SYNCRHONIZED_ANNOTATION = "umlp.jsweet.extension.annotation.SynchronizedGemList";

  public GUIComponentAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteType(TypeElement type) {
    if (isComponent(type)) {
      print("@Component({");
      print("selector: ");
      print("'" + pascalToKebab(type.getSimpleName().toString()) + "'");
      print("})").println().printIndent();
    }
    else if (isDirective(type)) {
      print("@Directive()").println().printIndent();
      importFrom("Directive", "@angular/core");
      importFrom("ActivatedRoute, Router", "@angular/router");
    }
    return super.substituteType(type);
  }

  @Override
  public boolean substituteVariable(VariableElement variable) {
    if (isInput(variable)) {
      importFrom("Input", "@angular/core");
      print("public ").print(variable.getSimpleName()).print("RoutingId : number;")
          .println().println().printIndent();
      print("@Input()").println().printIndent();
    }
    if (isOutput(variable)) {
      importFrom("Output", "@angular/core");
      print("@Output()").println().printIndent();
    }
    return super.substituteVariable(variable);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    TypeElement parentElement = getParentElement(TypeElement.class);
    if ((isDirective(parentElement) || isComponent(parentElement))
        && executable.getKind().toString().equals("CONSTRUCTOR")) {
      // handle Constrcuctor
      String cdName = getCDName(parentElement);
      String managerName = cdName + "Manager";
      String pathToManager = "../" + StringUtils.uncapitalize(cdName) + "/" + managerName;
      importFrom(managerName, pathToManager);
      printAdaptedConstructor(executable);
      printNgOnInit(executable, cdName);
      return true;
    } else if (isSynchronizedGemList(executable) && executable.getKind().toString().equals("METHOD")) {
      TypeMirror returnType = executable.getReturnType();
      if (types().isSubtype(types().erasure(returnType), types().erasure(util().getType(List.class)))) {
        printSynchronizedMethod(executable);
      }
    }
    return super.substituteExecutable(executable);
  }

  private void printSynchronizedMethod(ExecutableElement executable) {
    Name typeName = types().asElement(
        ((DeclaredType) executable.getReturnType()).getTypeArguments().get(0)).getSimpleName();
    print("public ").print(executable.getSimpleName()).print("_synchronized(")
        .printArgList((List<? extends ExtendedElement>) executable.getParameters())
        .print("): Iterable<")
        .print(typeName)
        .print("> {").println().startIndent().printIndent();
    print("let result: Iterable<" + typeName + "> = [];").println().printIndent();
    print("new Promise<Iterable<" + typeName + ">>(async resolve => {").println().startIndent().printIndent();
    print("(await this.").print(executable.getSimpleName())
        .print("(")
        .printArgList((List<? extends ExtendedElement>) executable.getParameters())
        .print(")).toArray(")
//        .printArgList((List<? extends ExtendedElement>) executable.getParameters())
        .print(").then(val => resolve(val));").println().endIndent().printIndent();
    print("}).then(val => { result = val; });").println().printIndent();
    print("return result;");
    endIndent().println().printIndent().print("}").println().println().printIndent();
  }

  private void printNgOnInit(ExecutableElement executable, String cdName) {
    println().println().printIndent().print("public ngOnInit(): void {").println().startIndent().printIndent();
    for (VariableElement parameter : executable.getParameters()) {
      Name typeSimpleName = Util.getElement(parameter.asType()).getSimpleName();
      print("if (this.").print(parameter.getSimpleName()).print(" == null) {").startIndent()
          .println().printIndent();
      print(cdName + "Manager.get" + typeSimpleName + "Sure(this.")
          .print(parameter.getSimpleName()).print("RoutingId).then(res => { ")
          .startIndent().println().printIndent()
          .print("this.").print(parameter.getSimpleName()).print(" = res;")
          .endIndent().println().printIndent().print("});");
      endIndent().println().printIndent().print("}").println().printIndent();
    }
    println().printIndent().print("this.init();");
    endIndent().println().printIndent().print("}");
  }

  private void printAdaptedConstructor(ExecutableElement executable) {
    print("public constructor(route: ActivatedRoute, protected router: Router) {");
    startIndent();
    for (VariableElement parameter : executable.getParameters()) {
      println().printIndent().
      print("this.").print(parameter.getSimpleName())
          .print("RoutingId = +route.snapshot.paramMap.get(\"")
          .print(parameter.getSimpleName()).print("RoutingId\");");
    }
    endIndent().println().printIndent().print("}");
  }

  @Override
  public void afterType(TypeElement type) {
    // Remove standard constructor
    if ((isDirective(type) || isComponent(type))) {
      int publicConstructor = getPrinter().getOutput().lastIndexOf("constructor()");
      int length = getPrinter().getOutput().length();
      getPrinter().removeLastChars(length - publicConstructor);
      println().print("}").println();
      print(type.getSimpleName()).print("[\"__class\"] = \"").print(type.getQualifiedName()).print("\";");
    }
  }

  protected String pascalToKebab(String input) {
    String regex = "([a-z])([A-Z]+)";
    String replacement = "$1-$2";
    return input.replaceAll(regex, replacement).toLowerCase();
  }

  @Override
  public Java2TypeScriptTranslator getPrinter() {
    if (super.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) super.getPrinter();
    } else {
      Log.error("0JS00001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }

  private boolean isComponent(TypeElement type) {
    return context.hasAnnotationType(type, COMPONENT_ANNOTATION);
  }

  private boolean isOutput(VariableElement variable) {
    return context.hasAnnotationType(variable, OUTPUT_ANNOTATION);
  }

  private boolean isInput(VariableElement variable) {
    return context.hasAnnotationType(variable, INPUT_ANNOTATION);
  }

  private boolean isDirective(TypeElement parentElement) {
    return context.hasAnnotationType(parentElement, DIRECTIVE_ANNOTATION);
  }

  private boolean isSynchronizedGemList(ExecutableElement executable) {
    return context.hasAnnotationType(executable, SYNCRHONIZED_ANNOTATION);
  }

  private void importFrom(String importedName, String pathToImportedClass) {
    getPrinter().useModule(
        new ModuleImportDescriptor(importedName, pathToImportedClass));
  }

  private String getCDName(TypeElement parentElement) {
    String annotation = isDirective(parentElement) ? DIRECTIVE_ANNOTATION : COMPONENT_ANNOTATION;
    String cdName = context.getAnnotationValue(parentElement, annotation, String.class, "");
    return cdName;
  }
}
