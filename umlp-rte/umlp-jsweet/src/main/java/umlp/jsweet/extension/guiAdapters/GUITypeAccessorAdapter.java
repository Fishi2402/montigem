/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.StringTransformations;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;

import javax.lang.model.type.TypeMirror;

public class GUITypeAccessorAdapter extends UMLPAbstractAdapter {
  public GUITypeAccessorAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation == null || invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    String targetType = invocation.getTargetType().toString();
    String methodName = invocation.getMethodName();

    if(isGuiComponentType(targetType)) {
      if (methodName.startsWith("get") && methodName.endsWith("List") && methodName.length() > 7) {
        String fieldName = StringTransformations.uncapitalize(methodName.replaceAll("^get(.*)List$", "$1"));
        print(invocation.getTargetExpression()).print(".").print(fieldName);
        return true;
      }
      else if (methodName.startsWith("set") && methodName.endsWith("List") && methodName.length() > 7) {
        String fieldName = StringTransformations.uncapitalize(methodName.replaceAll("^set(.*)List$", "$1"));
        print(invocation.getTargetExpression()).print(".").print(fieldName + " = ").printArgList(invocation.getArguments());
        return true;
      }
      else if (methodName.startsWith("get") && methodName.length() > 3) {
        String fieldName = StringTransformations.uncapitalize(methodName.replaceAll("^get(.*)$", "$1"));
        print(invocation.getTargetExpression()).print(".").print(fieldName);
        return true;
      }
      else if (methodName.startsWith("set") && methodName.length() > 3) {
        String fieldName = StringTransformations.uncapitalize(methodName.replaceAll("^set(.*)$", "$1"));
        print(invocation.getTargetExpression()).print(".").print(fieldName + " = ").printArgList(invocation.getArguments());
        return true;
      }
      else if (methodName.startsWith("add") && methodName.length() > 3) {
        String fieldName = StringTransformations.uncapitalize(methodName.replaceAll("^add(.*)$", "$1"));
        print(invocation.getTargetExpression()).print(".").print(fieldName + ".push(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
    }

    return super.substituteMethodInvocation(invocation);
  }

  @Override
  public TypeMirror mapType(TypeMirror type) {
    return super.mapType(type);
  }

  private static boolean isGuiComponentType(String targetType) {
    String[] parts = targetType.split("\\.");
    if (parts.length >= 4 && (parts[0] + "." + parts[1]).equals("mc.fenix")) {
      String relevantPackagePart = parts[2] + "." + parts[3];
      switch (relevantPackagePart) {
        case "basic.gemtreeviewtypes":
        case "charts.gembarcharttypes":
        case "charts.gemcandlestickcharttypes":
        case "charts.gemgaugecharttypes":
        case "charts.gemheatmapcharttypes":
        case "charts.gemlinecharttypes":
        case "charts.gemnetworkgraphtypes":
        case "charts.gempiecharttypes":
        case "charts.gemscatterplottypes":
        case "charts.gemsunburstcharttypes":
        case "charts.gemvenndiagramtypes":
        case "input.gemtextareainputtypes":
        case "input.gemtextinputtypes":
        case "networkgraph.networkgraphtypes":
        case "table.tabletypes":
          return true;
        default:
          return false;
      }
    }
    return false;
  }
}
