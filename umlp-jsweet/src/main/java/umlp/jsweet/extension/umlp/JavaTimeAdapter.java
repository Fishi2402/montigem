/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.AbstractAdapter;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.function.Function;

/**
 * This adapter is responsible to enable the usage of java.time and Date classes.
 */
public class JavaTimeAdapter extends AbstractAdapter {

  public JavaTimeAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeDateTypeMapping());
    addTypeMapping("java.time.LocalDateTime", "Date");
    addTypeMapping("java.time.LocalDate", "Date");
  }

  public Function<TypeMirror, String> computeDateTypeMapping() {
    return (TypeMirror type) -> {
      switch (type.toString()) {
        case "java.time.LocalDateTime":
        case "java.time.LocalDate":
        case "java.util.Date":
          return "Date";
      }
      return null;
    };
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    String targetType = invocation.getTargetType().toString();
    switch (targetType) {
      case "java.time.LocalDateTime":
      case "java.time.LocalDate":
      case "java.util.Date":
        if (substituteMethodInvocation_Date(invocation)) {
          return true;
        }
    }
    return super.substituteMethodInvocation(invocation);
  }

  /**
   * Helper method to ensure the target expression is wrapped in a JavaScript Date object.
   */
  private void printDateTarget(MethodInvocationElement invocation, boolean isLocalDateTime) {
    String targetSrc = invocation.getTargetExpression().toString();

    // Check if the target source string indicates it is already a JS Date object.
    boolean alreadyDateLike = targetSrc.contains("new Date(")
        || targetSrc.contains("Date.UTC(")
        || targetSrc.contains(".getTime()")
        || targetSrc.matches(".*\\bDate\\b.*");

    if (isLocalDateTime && !alreadyDateLike) {
      // Wrap the expression in 'new Date()' if it's a raw variable or method call.
      this.print("new Date(").print(invocation.getTargetExpression()).print(")");
    } else {
      // Print directly if it's already a Date object.
      this.print(invocation.getTargetExpression());
    }
  }

  private boolean substituteMethodInvocation_Date(MethodInvocationElement invocation) {
    String methodName = invocation.getMethodName();
    String targetType = invocation.getTargetType().toString();
    boolean isLocalDateTime = targetType.contains("LocalDateTime");

    // ==========================================================================================
    // FACTORY METHOD: LocalDateTime.of(...)
    // ==========================================================================================
    if ("of".equals(methodName)) {
      printMacroName(methodName);

      // Use UTC to create the date so browser timezone doesn't shift the values.
      this.print("new Date(Date.UTC(");

      List<ExtendedElement> args = invocation.getArguments();
      if (args.size() >= 3) {
        this.print(args.get(0)).print(", ");       // Year
        this.print(args.get(1)).print(" - 1, ");   // Subtract 1 because Java months are 1-12, JS are 0-11.
        this.print(args.get(2));                   // Day

        // Handle optional arguments for Hour, Minute, and Second, defaulting to 0.
        this.print(", ").print(args.size() >= 4 ? args.get(3).toString() : "0");
        this.print(", ").print(args.size() >= 5 ? args.get(4).toString() : "0");
        this.print(", ").print(args.size() >= 6 ? args.get(5).toString() : "0");

        // Convert nanoseconds (Java) to milliseconds (JS), losing precision.
        if (args.size() >= 7) {
          this.print(", Math.floor(").print(args.get(6)).print(" / 1000000)");
        } else {
          this.print(", 0");
        }
      }
      this.print("))");
      return true;
    }

    // ==========================================================================================
    // plus(Duration) / minus(Duration)
    // ==========================================================================================
    if (invocation.getArguments().size() == 1 &&
        ("plus".equals(methodName) || "minus".equals(methodName))) {
      String targetOp = "plus".equals(methodName) ? "addTo" : "subtractFrom";
      printMacroName(methodName);
      this.print(invocation.getArguments().get(0)).print("." + targetOp + "(");
      printDateTarget(invocation, isLocalDateTime);
      this.print(")");
      return true;
    }

    // ==========================================================================================
    // plusDays, minusHours, plusMonths, etc.
    // ==========================================================================================
    boolean isPlus = methodName.startsWith("plus");
    boolean isMinus = methodName.startsWith("minus");

    if (isPlus || isMinus) {
      String baseName = methodName.replace("plus", "").replace("minus", "");

      String jsGetter = null;
      String jsSetter = null;
      long factor = 1;

      switch (baseName) {
        case "Years":   jsGetter = "getUTCFullYear"; jsSetter = "setUTCFullYear"; break;
        case "Months":  jsGetter = "getUTCMonth";    jsSetter = "setUTCMonth";    break;
        case "Weeks":   jsGetter = "getUTCDate";     jsSetter = "setUTCDate"; factor = 7; break;
        case "Days":    jsGetter = "getUTCDate";     jsSetter = "setUTCDate";     break;
        case "Hours":   jsGetter = "getUTCHours";    jsSetter = "setUTCHours";    break;
        case "Minutes": jsGetter = "getUTCMinutes";  jsSetter = "setUTCMinutes";  break;
        case "Seconds": jsGetter = "getUTCSeconds";  jsSetter = "setUTCSeconds";  break;
        case "Nanos":   jsGetter = "getUTCMilliseconds"; jsSetter = "setUTCMilliseconds"; break;
      }

      printMacroName(methodName);

      this.print("((d) => { ");
      this.print("let res = new Date(d.getTime()); ");

      if ("Months".equals(baseName) || "Years".equals(baseName)) {
        this.print("let checkDay = res.getUTCDate(); ");
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + " : " - ");
        this.print(invocation.getArguments().get(0));
        this.print("); ");
        this.print("if (res.getUTCDate() !== checkDay) { res.setUTCDate(0); } ");
      } else if ("Nanos".equals(baseName)) {
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + Math.floor(" : " - Math.floor(");
        this.print(invocation.getArguments().get(0)).print(" / 1000000)");
        this.print("); ");
      } else {
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + (" : " - (");
        this.print(invocation.getArguments().get(0));
        if (factor > 1) this.print(" * " + factor);
        this.print(")); ");
      }

      this.print("return res; })(");
      printDateTarget(invocation, isLocalDateTime);
      this.print(")");
      return true;
    }

    // ==========================================================================================
    // isAfter, isBefore, isEqual
    // ==========================================================================================
    if ("isAfter".equals(methodName) || "isBefore".equals(methodName) || "isEqual".equals(methodName)) {
      printMacroName(methodName);
      this.print("(");
      printDateTarget(invocation, isLocalDateTime);
      this.print(".getTime()");

      if ("isAfter".equals(methodName))       this.print(" > ");
      else if ("isBefore".equals(methodName)) this.print(" < ");
      else                                    this.print(" === ");

      if (isLocalDateTime) this.print("new Date(");
      this.print(invocation.getArguments().get(0));
      if (isLocalDateTime) this.print(")");
      this.print(".getTime())");
      return true;
    }

    // ==========================================================================================
    // getHour, getMinute, etc.
    // ==========================================================================================
    String jsSimpleMethod = switch (methodName) {
      case "getHour", "getHours" -> "getUTCHours";
      case "getMinute", "getMinutes" -> "getUTCMinutes";
      case "getSecond", "getSeconds" -> "getUTCSeconds";
      case "getYear" -> "getUTCFullYear";
      case "getDayOfMonth", "getDate" -> "getUTCDate";
      default -> null;
    };

    if (jsSimpleMethod != null) {
      printMacroName(methodName);
      printDateTarget(invocation, isLocalDateTime);
      this.print(".").print(jsSimpleMethod).print("()");
      return true;
    }

    // ==========================================================================================
    // COMPLEX GETTERS & ENUMS
    // ==========================================================================================
    switch (methodName) {
      case "getMonthValue":
        printMacroName(methodName);
        this.print("(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getUTCMonth() + 1)");
        return true;

      case "getMonth":
        printMacroName(methodName);
        this.print("[\"JANUARY\", \"FEBRUARY\", \"MARCH\", \"APRIL\", \"MAY\", \"JUNE\", \"JULY\", \"AUGUST\", \"SEPTEMBER\", \"OCTOBER\", \"NOVEMBER\", \"DECEMBER\"][");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getMonth()]");
        return true;

      case "getDayOfWeek":
        printMacroName(methodName);
        this.print("[\"SUNDAY\", \"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\", \"SATURDAY\"][");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getDay()]");
        return true;

      case "getDayOfYear":
        printMacroName(methodName);
        this.print("((d) => { ");
        this.print("let utcNow = Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate()); ");
        this.print("let utcStart = Date.UTC(d.getUTCFullYear(), 0, 0); ");
        this.print("return Math.floor((utcNow - utcStart) / 86400000); })(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(")");
        return true;

      case "getNano":
        printMacroName(methodName);
        this.print("(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getUTCMilliseconds() * 1000000)");
        return true;

      case "toString":
        printMacroName(methodName);
        this.print("((d) => { ");
        this.print("  const pad = (n) => ('0' + n).slice(-2); ");
        this.print("  let s = d.getUTCFullYear() + '-' + ");
        this.print("         pad(d.getUTCMonth() + 1) + '-' + ");
        this.print("         pad(d.getUTCDate()); ");

        if (!targetType.equals("java.time.LocalDate")) {
          this.print("  s += 'T' + ");
          this.print("         pad(d.getUTCHours()) + ':' + ");
          this.print("         pad(d.getUTCMinutes()) + ':' + ");
          this.print("         pad(d.getUTCSeconds()); ");
          this.print("  if (d.getUTCMilliseconds() > 0) { ");
          this.print("    s += '.' + ('00' + d.getUTCMilliseconds()).slice(-3); ");
          this.print("  } ");
        }
        this.print("  return s; ");
        this.print("})(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(")");
        return true;

      case "now":
        this.print("((() => { ");
        this.print("  const now = new Date(); ");
        this.print("  return new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds(), now.getMilliseconds())); ");
        this.print("})())");
        return true;

      case "parse":
        printMacroName(methodName);
        this.print("new Date(");
        this.print(invocation.getArguments().get(0));
        this.print(" + 'Z')");
        return true;

      case "compareTo":
        print("(").print(invocation.getTargetExpression()).print(".getTime() - ")
            .printArgList(invocation.getArguments()).print(".getTime())");
        return true;

      case "toEpochSecond":
        print(invocation.getTargetExpression()).print(".getTime() / 1000");
        return true;

      case "ofEpochSecond":
        print("new Date((").print(invocation.getArguments().get(0)).print(") * 1000)");
        return true;

      case "toLocalDate":
      case "toLocalTime":
        print(invocation.getTargetExpression());
        return true;

      case "plusYears":
      case "plusMonths":
      case "plusWeeks":
      case "plusDays":
      case "plusHours":
      case "plusMinutes":
      case "plusSeconds":
      case "minusYears":
      case "minusMonths":
      case "minusWeeks":
      case "minusDays":
      case "minusHours":
      case "minusMinutes":
      case "minusSeconds":
      case "atStartOfDay":
      case "withYear":
      case "withMonth":
      case "withDayOfMonth":
      case "withDayOfYear":
      case "withHour":
      case "withMinute":
      case "withSecond":
        print("LocalDateTimeHelper." + invocation.getMethodName() + "(")
            .print(invocation.getTargetExpression()).print(", ")
            .printArgList(invocation.getArguments()).print(")");
        getPrinter().useModule(
            new ModuleImportDescriptor("LocalDateTimeHelper", "@umlp/commonj2ts"));
        return true;
    }
    return false;
  }
}
