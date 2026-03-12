/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.ExtendedElement;
import umlp.jsweet.extension.AbstractAdapter;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import de.se_rwth.commons.logging.Log;


public class JavaStreamAdapter extends AbstractAdapter {

  public JavaStreamAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  protected static final Set<String> SUPPORTED_STREAM_METHODS = new HashSet<>(Arrays.asList(
      "filter", "map", "flatMap", "distinct", "sorted", "peek", "limit", "skip",
      "forEach", "forEachOrdered", "toArray", "reduce", "collect",
      "min", "max", "count", "anyMatch", "allMatch", "noneMatch",
      "findFirst", "findAny", "empty", "of", "range", "rangeClosed",
      "toList"
  ));

  protected static final Set<String> NUMERICAL_MAP_METHODS = new HashSet<>(Arrays.asList(
      "mapToInt", "mapToLong", "mapToDouble",
      "flatMapToInt", "flatMapToLong", "flatMapToDouble"
  ));

  @Override
  protected void initStaticLibrary() {
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    String methodName = invocation.getMethodName();

    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    String targetTypeFqn = getFqn(invocation.getTargetType());


    // 1. collection.stream() -> Stream.of(await collection.toArraySync())
    if (methodName.equals("stream") && isCollection(invocation.getTargetType())) {
      boolean isAsync = isAsyncCollection(invocation.getTargetType());
      print("Stream.of(");
      if (isAsync) {
        print("await ");
      }
      if (invocation.getTargetExpression() != null) {
        print(invocation.getTargetExpression());
      } else {
        print("this");
      }
      if (isAsync) {
        print(".toArraySync()");
      }
      print(")");
      getPrinter().useModule(new ModuleImportDescriptor("Stream", getLibraryLocation()));
      return true;
    }

    // 2. Stream methods
    if (isStream(invocation.getTargetType())) {
      // Check for supported methods
      if (!SUPPORTED_STREAM_METHODS.contains(methodName) && !NUMERICAL_MAP_METHODS.contains(methodName)) {
        Log.error("0xEE010: Stream method '" + methodName + "' is currently not supported by our transpilation.");
      }

      if (methodName.equals("of") || methodName.equals("empty") || methodName.equals("range") || methodName.equals("rangeClosed")) {
        print("Stream.").print(methodName).print("(").printArgList(invocation.getArguments()).print(")");
        getPrinter().useModule(new ModuleImportDescriptor("Stream", getLibraryLocation()));
        return true;
      }

      // Map mapToInt etc. to simple map/flatMap
      if (NUMERICAL_MAP_METHODS.contains(methodName)) {
        String newName = methodName.startsWith("flatMap") ? "flatMap" : "map";
        print(".").print(newName).print("(").printArgList(invocation.getArguments()).print(")");
        return true;
      }

      // Optimization: .collect(Collectors.toList())
      if (methodName.equals("collect") && invocation.getArgumentCount() == 1) {
        boolean isToList = false;
        if (invocation.getArgument(0) instanceof MethodInvocationElement) {
          MethodInvocationElement arg = (MethodInvocationElement) invocation.getArgument(0);
          if (arg.getMethodName().equals("toList") &&
              arg.getTargetType() != null &&
              getFqn(arg.getTargetType()).contains("Collectors")) {
            isToList = true;
          }
        }

        if (isToList) {
          print(invocation.getTargetExpression());
          print(".collect(Collectors.toList())");
          getPrinter().useModule(new ModuleImportDescriptor("Collectors", getLibraryLocation()));
          return true;
        }
      }
    }

    // 3. Collectors.toList() -> Collectors.toList()
    if (targetTypeFqn.equals("java.util.stream.Collectors")) {
      if (methodName.equals("toList")) {
        print("Collectors.toList()");
        getPrinter().useModule(new ModuleImportDescriptor("Collectors", getLibraryLocation()));
        return true;
      }
    }

    return super.substituteMethodInvocation(invocation);
  }

  @Override
  public boolean substituteInstanceof(String exprStr, ExtendedElement expr, TypeMirror type) {
    if (type != null && isStream(type)) {
      print("(").print(exprStr).print(" != null)");
      return true;
    }
    return super.substituteInstanceof(exprStr, expr, type);
  }

  private String getFqn(TypeMirror type) {
    return types().erasure(type).toString();
  }

  private boolean isAsyncCollection(TypeMirror type) {
    String fqn = getFqn(type);
    return fqn.contains("GemList") || fqn.contains("IUMLPList");
  }

  private boolean isStream(TypeMirror type) {
    String fqn = getFqn(type);
    return fqn.contains("stream.Stream") ||
        fqn.contains("stream.IntStream") ||
        fqn.contains("stream.LongStream") ||
        fqn.contains("stream.DoubleStream");
  }

  private boolean isCollection(TypeMirror type) {
    String fqn = getFqn(type);
    return fqn.contains("java.util.List") ||
        fqn.contains("java.util.Collection") ||
        fqn.contains("java.util.ArrayList") ||
        fqn.contains("GemList") ||
        fqn.contains("IUMLPList");
  }
}
