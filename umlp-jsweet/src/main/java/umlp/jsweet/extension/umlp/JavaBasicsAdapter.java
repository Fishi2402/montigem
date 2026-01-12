/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;
import standalone.com.sun.source.tree.LambdaExpressionTree;
import standalone.com.sun.source.tree.Tree;
import umlp.jsweet.extension.AbstractAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This adapter is responsible to enable the usage of some additional {@link java.util} classes.
 */
public class JavaBasicsAdapter extends AbstractAdapter {

  public JavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeJavaOptionalTypeMapping());
    getTypeMirrorMappings().add(0, computeDateTypeMapping());
    addTypeMapping("java.time.LocalDateTime", "Date");
    addTypeMapping("java.time.LocalDate", "Date");
  }

  public Function<TypeMirror, String> computeJavaOptionalTypeMapping() {
    return (TypeMirror type) -> {
      TypeMirror mappedType = mapOptionalType(type);
      if (mappedType != null) {
        return this.getMappedType(mappedType);
      }
      return null;
    };
  }

  public TypeMirror mapOptionalType(TypeMirror type) {
    if (this.types().isSubtype(this.types().erasure(type), this.types().erasure(
        this.util().getType(Optional.class)))
        && type instanceof DeclaredType) {
      List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();
      if (typeArgs.size() > 0) {
        TypeMirror backingType = typeArgs.get(0);
        if (backingType.getKind() == TypeKind.DECLARED) {
          return backingType;
        }
      }
    }
    return null;
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

  /**
   * Can be used to adapt method invocations like {@code Objects.requireNonNull(...)}
   */
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    String targetType = invocation.getTargetType().toString();
    switch (targetType) {
      case "java.util.Objects":
        if (substituteMethodInvocation_JavaUtilObjects(invocation)) {
          return true;
        }
        break;
      case "java.lang.String":
        if (substituteMethodInvocation_JavaLangString(invocation)) {
          return true;
        }
        break;
      case "java.time.LocalDateTime":
      case "java.time.LocalDate":
      case "java.util.Date":
        if (substituteMethodInvocation_Date(invocation)) {
          return true;
        }
    }

    String targetClassName = invocation.getMethod().getEnclosingElement().toString();
    ExtendedElement targetExpression = invocation.getTargetExpression();
    if (targetExpression != null && targetExpression.getTypeAsElement() != null) {
      targetClassName = targetExpression.getTypeAsElement().toString();
    }
    if (targetClassName != null
        && (targetExpression != null || invocation.getMethod().getModifiers().contains(
        Modifier.STATIC))) {
      switch (targetClassName) {
        case "java.util.Optional":
          if (substituteMethodInvocation_Optional(invocation)) {
            return true;
          }
      }
    }

    return super.substituteMethodInvocation(invocation);
  }

  private boolean substituteMethodInvocation_Optional(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "or":
        Tree argumentTree = ExtendedElementFactory.toTree(invocation.getArgument(0));
        boolean isAsync = argumentTree instanceof LambdaExpressionTree &&
            context.isAsyncLambda((LambdaExpressionTree) argumentTree);
        printMacroName(invocation.getMethodName());
        if (isAsync) {
          print("(await ");
        }
        print("((v, get) => v == null ? get() : v)(").print(invocation.getTargetExpression())
            .print(",");
//        if (isAsync) {
//          print("async ");
//        }
        print(invocation.getArgument(0)).print(")");
        if (isAsync) {
          print(")");
        }
        return true;
    }
    return false;
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
    // Map generic plus/minus calls to specific helper functions in the JS environment.
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

      // Map Java units to specific JS UTC methods to ensure 24h days.
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

      // Create an Immediately Invoked Function Expression to clone the date, preserving Java's immutability contract.
      this.print("((d) => { ");
      this.print("let res = new Date(d.getTime()); ");

      if ("Months".equals(baseName) || "Years".equals(baseName)) {
        // Remember the original day to detect invalid overflow (e.g., Jan 31 -> Feb 28).
        this.print("let checkDay = res.getUTCDate(); ");
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + " : " - ");
        this.print(invocation.getArguments().get(0));
        this.print("); ");

        // Fix overflow: if day changed unexpectedly, reset to last valid day of previous month.
        this.print("if (res.getUTCDate() !== checkDay) { res.setUTCDate(0); } ");
      } else if ("Nanos".equals(baseName)) {
        // Convert nanos to millis before adding/subtracting.
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + Math.floor(" : " - Math.floor(");
        this.print(invocation.getArguments().get(0)).print(" / 1000000)");
        this.print("); ");
      } else {
        // update value using the getter, operator, and argument.
        this.print("res." + jsSetter + "(res." + jsGetter + "()");
        this.print(isPlus ? " + (" : " - (");
        this.print(invocation.getArguments().get(0));
        if (factor > 1) this.print(" * " + factor); // Handle Weeks (multiply days by 7).
        this.print(")); ");
      }

      this.print("return res; })(");
      printDateTarget(invocation, isLocalDateTime); // Pass the original object into the function.
      this.print(")");
      return true;
    }

    // ==========================================================================================
    // isAfter, isBefore, isEqual
    // ==========================================================================================
    if ("isAfter".equals(methodName) || "isBefore".equals(methodName) || "isEqual".equals(methodName)) {
      printMacroName(methodName);

      // Compare raw timestamps (integers) for speed and correctness.
      this.print("(");
      printDateTarget(invocation, isLocalDateTime);
      this.print(".getTime()");

      if ("isAfter".equals(methodName))       this.print(" > ");
      else if ("isBefore".equals(methodName)) this.print(" < ");
      else                                    this.print(" === ");

      // Ensure the comparison argument is also a Date/Timestamp.
      if (isLocalDateTime) this.print("new Date(");
      this.print(invocation.getArguments().get(0));
      if (isLocalDateTime) this.print(")");
      this.print(".getTime())");
      return true;
    }

    // ==========================================================================================
    // getHour, getMinute, etc.
    // ==========================================================================================
    // Map standard getters to UTC versions because we store "Local" time in UTC slots.
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
        // Convert JS 0-indexed month back to Java 1-indexed.
        this.print(".getUTCMonth() + 1)");
        return true;

      case "getMonth":
        printMacroName(methodName);
        // Return month name from a hardcoded string array (quick Enum simulation).
        this.print("[\"JANUARY\", \"FEBRUARY\", \"MARCH\", \"APRIL\", \"MAY\", \"JUNE\", \"JULY\", \"AUGUST\", \"SEPTEMBER\", \"OCTOBER\", \"NOVEMBER\", \"DECEMBER\"][");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getMonth()]");
        return true;

      case "getDayOfWeek":
        printMacroName(methodName);
        // Return day name from a hardcoded string array (Sunday is index 0).
        this.print("[\"SUNDAY\", \"MONDAY\", \"TUESDAY\", \"WEDNESDAY\", \"THURSDAY\", \"FRIDAY\", \"SATURDAY\"][");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getDay()]");
        return true;

      case "getDayOfYear":
        printMacroName(methodName);
        // Calculate day of year by getting difference between Now and Jan 1st.
        this.print("((d) => { ");
        this.print("let utcNow = Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate()); ");
        this.print("let utcStart = Date.UTC(d.getUTCFullYear(), 0, 0); ");
        this.print("return Math.floor((utcNow - utcStart) / 86400000); })(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(")");
        return true;

      case "getNano":
        printMacroName(methodName);
        // Fake nanoseconds by multiplying milliseconds by 1,000,000.
        this.print("(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(".getUTCMilliseconds() * 1000000)");
        return true;

      case "toString":
        printMacroName(methodName);
        this.print("((d) => { ");
        this.print("  const pad = (n) => ('0' + n).slice(-2); ");

        // 1. Build ISO Date part (YYYY-MM-DD)
        this.print("  let s = d.getUTCFullYear() + '-' + ");
        this.print("         pad(d.getUTCMonth() + 1) + '-' + ");
        this.print("         pad(d.getUTCDate()); ");

        // 2. Append Time part ONLY if it is NOT a LocalDate
        // (LocalDate should just be YYYY-MM-DD for HTML5 input compatibility)
        if (!targetType.equals("java.time.LocalDate")) {
          this.print("  s += 'T' + ");
          this.print("         pad(d.getUTCHours()) + ':' + ");
          this.print("         pad(d.getUTCMinutes()) + ':' + ");
          this.print("         pad(d.getUTCSeconds()); ");

          // Append milliseconds if present
          this.print("  if (d.getUTCMilliseconds() > 0) { ");
          this.print("    s += '.' + ('00' + d.getUTCMilliseconds()).slice(-3); ");
          this.print("  } ");
        }

        this.print("  return s; ");
        this.print("})(");
        printDateTarget(invocation, isLocalDateTime);
        this.print(")");
        return true;

      // ==========================================================================================
      // now(), parse(), compareTo(), Epoch
      // ==========================================================================================
      case "now":
        // Capture browser's local time components and store them as UTC values.
        this.print("((() => { ");
        this.print("  const now = new Date(); ");
        this.print("  return new Date(Date.UTC(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds(), now.getMilliseconds())); ");
        this.print("})())");
        return true;

      case "parse":
        printMacroName(methodName);
        // Fix: Parse ISO string. Append 'Z' to force JS to parse it as UTC.
        this.print("new Date(");
        this.print(invocation.getArguments().get(0));
        this.print(" + 'Z')");
        return true;

      case "compareTo":
        // Compare based on absolute epoch milliseconds.
        print("(").print(invocation.getTargetExpression()).print(".getTime() - ")
            .printArgList(invocation.getArguments()).print(".getTime())");
        return true;

      case "toEpochSecond":
        // Return seconds from epoch based on the stored UTC time.
        print(invocation.getTargetExpression()).print(".getTime() / 1000");
        return true;

      case "ofEpochSecond":
        // Create a new Date from seconds since epoch.
        print("new Date((").print(invocation.getArguments().get(0)).print(") * 1000)");
        return true;

      case "toLocalDate":
      case "toLocalTime":
        // Since both LocalDateTime and LocalDate map to JS Date, we return the object as-is.
        print(invocation.getTargetExpression());
        return true;

      // === Integration of LocalDateTimeHelper ===
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

  public boolean substituteMethodInvocation_JavaUtilObjects(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "requireNonNull":
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        this.print("/* requireNonNull */")
            .print(String.format(
                "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (",
                msg))
            .print(invocation.getArgument(0))
            .print(")");
        return true;
      case "equals":
        boolean isDate = false;
        for (ExtendedElement argument : invocation.getArguments()) {
          switch (argument.getType().toString()) {
            case "java.time.LocalDateTime":
            case "java.time.LocalDate":
            case "java.util.Date":
              isDate = true;
          }
        }
        if (isDate) {
          this.print("/* equals */")
              .print("((a, b) => { return a.getTime() === b.getTime(); }) (")
              .printArgList(invocation.getArguments())
              .print(")");
        }
        else {
          this.print("/* equals */")
              .print("((a, b) => { return a === b; }) (")
              .printArgList(invocation.getArguments())
              .print(")");
        }
        return true;
    }
    return false;
  }

  public boolean substituteMethodInvocation_JavaLangString(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "format":
        if (invocation.getMethodName().equals("format")) {
          this.print("StringHelper.format(").printArgList(invocation.getArguments()).print(")");
          return true;
        }
      case "isBlank": {
        print("(");
        print(invocation.getTargetExpression());
        print(".trim().length === 0)");
        return true;
      }
    }
    return false;
  }

  @Override
  public TypeMirror mapType(TypeMirror type) {
    return mapOptionalType(type);
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
    putToStaticLibrary("java.lang.String", "StringHelper");
  }

}
