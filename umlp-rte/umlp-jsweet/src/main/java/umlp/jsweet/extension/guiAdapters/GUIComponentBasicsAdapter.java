/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

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
    if (adapterUtils.hasDirectiveAnnotation(type)) {
      println().printIndent().print("protected router: Router;").println().println();
    }
    if (adapterUtils.isHWCComponentOrDirective(type)) {
      String cdName = adapterUtils.getAnnotationPropertyValue(type, "cdName", String.class, "");
      String managerName = cdName + "Manager";
      String pathToManager = StringUtils.lowerCase(cdName) + "/" + managerName;
      adapterUtils.importFromWithModule(managerName, pathToManager);
    }

    super.beforeTypeBody(type);
  }

  @Override
  public boolean substituteType(TypeElement classElement) {
    if (adapterUtils.isHWCComponent(classElement) && !adapterUtils.hasComponentAnnotation(classElement)) {
      Log.warn("0JSC003 " + classElement.getQualifiedName().toString()
          + " Handwritten Adaption of a GUI Component should be tagged with @Component()");
    }
    if (adapterUtils.isHWCComponent(classElement)) {
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
    else if (adapterUtils.hasDirectiveAnnotation(classElement)) {
      print("@Directive()").println().printIndent();
      adapterUtils.importFromWithModule("Directive", "@angular/core");
    }
    if (adapterUtils.isHWCComponentOrDirective(classElement)) {
      adapterUtils.importFromWithModule("ActivatedRoute, Router", "@angular/router");
      adapterUtils.importFromWithModule("ComponentObserverManager", GUIAdapterUtils.UMLP_COMMON_POINTER);
    }
    return super.substituteType(classElement);
  }

  @Override
  public boolean substituteExtends(TypeElement type) {
    if (adapterUtils.isHWCofDirective(type)) {
      String componentName = type.getSimpleName().toString() + "Component";
      print(" extends " + componentName);
      String path = type.getQualifiedName().toString().replace(".", "/");

      adapterUtils.importFromWithModule(componentName + ", config", path + "Component");
      adapterUtils.importFromWithModule("ActivatedRoute, Router", "@angular/router");
      return true;
    }
    return super.substituteExtends(type);
  }

}
