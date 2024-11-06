/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;

/**
 * Adapting generated GUI components for angular
 */
public class GUIComponentBasicsAdapter extends PrinterAdapter {
  private final GUIAdapterUtils adapterUtils;

  public GUIComponentBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (adapterUtils.isDirective(type)) {
      println().printIndent().print("protected router: Router;").println().println();
    }
    if (adapterUtils.isDirective(type) || adapterUtils.isComponent(type) || adapterUtils.isHWCofComponent(type)) {
      String cdName = adapterUtils.getAnnotationPropertyValue(type, "cdName", String.class, "");
      String managerName = cdName + "Manager";
      String pathToManager = "@src/" + StringUtils.lowerCase(cdName) + "/" + managerName;
      adapterUtils.importFromWithModule(managerName, pathToManager);
    }

    super.beforeTypeBody(type);
  }

  @Override
  public boolean substituteType(TypeElement classElement) {
    if (adapterUtils.isComponent(classElement)) {
      Boolean standAlone = adapterUtils.getAnnotationPropertyValue(classElement, "standalone", Boolean.class,
          true);
      String[] imports = adapterUtils.getAnnotationPropertyValue(classElement, "imports",
          String[].class, new String[] { "...config.imports" });
      String selector = adapterUtils.getAnnotationPropertyValue(classElement, "selector", String.class,
          "config.selector");
      String templateUrl = adapterUtils.getAnnotationPropertyValue(classElement, "templateUrl", String.class,
          "config.templateUrl");
      String styles = adapterUtils.getAnnotationPropertyValue(classElement, "styles", String.class,
          "config.styles");
      String[] styleUrls = adapterUtils.getAnnotationPropertyValue(classElement, "styleUrls",
          String[].class, new String[] { "...config.styleUrls" });
      String[] tsImports = adapterUtils.getAnnotationPropertyValue(classElement, "tsImports",
          String[].class, new String[] {});
      print("@Component({").println().startIndent().printIndent()
          .print("standalone: " + standAlone + ",").println().printIndent()
          .print("imports: " + Arrays.toString(imports) + ",").println().printIndent()
          .print("selector: " + selector + ",").println().printIndent()
          .print("templateUrl: " + templateUrl + ",").println().printIndent()
          .print("styles: " + styles + ",").println().printIndent()
          .print("styleUrls: " + Arrays.toString(styleUrls)).println().endIndent()
          .print("})").println().printIndent();
      adapterUtils.importFromWithModule("Component", "@angular/core");

      for (String componentImport : tsImports) {
        String[] split = componentImport.split(",");
        if (split.length != 2) {
          Log.error("0JSC004 " + componentImport + " is not in the correct format (importName, importFrom)");
        }
        adapterUtils.importFromWithModule(split[0].strip(), split[1].strip());
      }
    }
    else if (adapterUtils.isHWCofComponent(classElement)) {
      Log.error("0JSC003 " + classElement.getQualifiedName().toString()
          + " Handwritten Adaption of a GUI Component must be tagged with @Component()");
    }
    else if (adapterUtils.isDirective(classElement)) {
      print("@Directive()").println().printIndent();
      adapterUtils.importFromWithModule("Directive", "@angular/core");
    }
    if (adapterUtils.isDirective(classElement) || adapterUtils.isComponent(classElement)) {
      adapterUtils.importFromWithModule("ActivatedRoute, Router", "@angular/router");
      adapterUtils.importFromWithModule("ComponentObserverManager", GUIAdapterUtils.UMLP_COMMON_POINTER);
    }
    return super.substituteType(classElement);
  }

  @Override
  public boolean substituteExtends(TypeElement type) {
    if (adapterUtils.isHWCofComponent(type)) {
      String componentName = type.getSimpleName().toString() + "Component";
      print(" extends " + componentName);
      String path = type.getQualifiedName().toString().replace(".", "/");

      adapterUtils.importFromWithModule(componentName + ", config", "@src/" + path + "Component");
      adapterUtils.importFromWithModule("ActivatedRoute, Router", "@angular/router");
      return true;
    }
    return super.substituteExtends(type);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
//    TypeElement classElement = getParentElement(TypeElement.class);
//    if (adapterUtils.isComponentConstructor(executable, classElement)) {
//      // Don't print the constructor
//      return true;
//    }
    return super.substituteExecutable(executable);
  }

  @Override
  public void afterType(TypeElement type) {
    // Remove standard constructor
//    if (adapterUtils.isDirective(type) || adapterUtils.isComponent(type) || adapterUtils.isHWCofComponent(type)) {
//      int publicConstructor = getPrinter().getOutput().lastIndexOf("constructor()");
//      if (publicConstructor < 0) {
//        return;
//      }
//      int length = getPrinter().getOutput().length();
//      getPrinter().removeLastChars(length - publicConstructor);
//      println().print("}").println();
//      print(type.getSimpleName()).print("[\"__class\"] = \"").print(type.getQualifiedName())
//          .print("\";");
//    }
  }
}
