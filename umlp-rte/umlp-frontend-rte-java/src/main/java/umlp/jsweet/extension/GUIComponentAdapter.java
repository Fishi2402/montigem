package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;

import javax.lang.model.element.TypeElement;

public class GUIComponentAdapter extends PrinterAdapter {

  public static final String COMPONENT_ANNOTATION = "umlp.jsweet.extension.annotation.Component";
  public static final String DIRECTIVE_ANNOTATION = "umlp.jsweet.extension.annotation.Directive";

  public GUIComponentAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteType(TypeElement type) {
    if (context.hasAnnotationType(type, COMPONENT_ANNOTATION)) {
      print("@Component({");
      print("selector: ");
      print("'" + pascalToKebab(type.getSimpleName().toString()) + "'");
      print("})");
    }
    else if (context.hasAnnotationType(type, DIRECTIVE_ANNOTATION)) {
      print("@Directive()");
    }
    return super.substituteType(type);
  }

  protected String pascalToKebab(String input) {
    String regex = "([a-z])([A-Z]+)";
    String replacement = "$1-$2";
    return input.replaceAll(regex, replacement).toLowerCase();
  }

}
