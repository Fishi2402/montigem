/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.AbstractTreePrinter;
import standalone.com.sun.source.tree.Tree;
import umlp.jsweet.extension.util.GUIAdapterUtils;
import umlp.jsweet.extension.util.RTEPathUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

public class DelegateGemListToArrayAdapter extends PrinterAdapter {
  private final GUIAdapterUtils adapterUtils;

  public DelegateGemListToArrayAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public TypeMirror mapType(TypeMirror type) {
    if(types().erasure(type).toString().equals(RTEPathUtil.getBackendRTECommonPath("GemList"))) {
      return this.util().getType(java.util.List.class);
    }
    return super.mapType(type);
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (adapterUtils.isDirective(type)) {
      printIndent().print("protected toArray<T>(arr: IUMLPList<T> | Array<T>): Array<T> {")
          .println().startIndent().printIndent()
          .print("return GemListUtil.toArray(arr);")
          .println().endIndent().printIndent().print("}").println().println();
      adapterUtils.importFromWithModule("IUMLPList", GUIAdapterUtils.UMLP_COMMON_POINTER);
    }
    super.beforeTypeBody(type);
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    boolean shouldPrintToArray = false;

    Element invocatedType = types().asElement(invocation.getType());
    if (shouldPrintToArray(invocatedType, getPrinter(), adapterUtils)) {
      shouldPrintToArray = true;
    }

    if (shouldPrintToArray) {
      print("(await GemListUtil.toArraySync(");
    }
    boolean res = super.substituteMethodInvocation(invocation);
    if (shouldPrintToArray) {
      print("))");
    }

    return res;
  }

  public static boolean shouldPrintToArray(Element invocatedType, AbstractTreePrinter printer, GUIAdapterUtils adapterUtils) {
    return invocatedType != null && invocatedType instanceof TypeElement &&
        adapterUtils.isSubTypeOf((TypeElement) invocatedType, "java.util.List") &&
        !adapterUtils.isSubTypeOf((TypeElement) invocatedType, "umlp.backendrte.common.UMLPArrayList") &&
        !printer.getParent().getKind().equals(Tree.Kind.EXPRESSION_STATEMENT);
  }
}
