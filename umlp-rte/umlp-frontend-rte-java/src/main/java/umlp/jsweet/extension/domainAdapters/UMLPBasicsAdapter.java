/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.model.TypeCastElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

/**
 * This adapter is responsible to correctly map Elements from the MontiCore world to correct
 *  TS implementaitons.
 * This is done mainly forde.monticore.symboltable.serialization.json.* and de.se_rwth.commons.logging.Log
 */
public class UMLPBasicsAdapter extends UMLPAbstractAdapter {

  public UMLPBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeMCJSonTypeMapping());
  }

  public static Function<TypeMirror, String> computeMCJSonTypeMapping() {
    return (TypeMirror type) -> {
      if (type.toString().equals("de.monticore.symboltable.serialization.json.JsonObject")) {
        return "any";
      }
      if (type.toString().equals("de.monticore.symboltable.serialization.json.JsonElement")) {
        return "any";
      }
      return null;
    };
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("de.se_rwth.commons.logging.Log");
    addToStaticLibrary("de.monticore.symboltable.serialization.JsonPrinter");
    addToStaticLibrary("de.se_rwth.commons.StringTransformations");
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }

    String targetType = invocation.getTargetType().toString();
    String methodName = invocation.getMethodName();
    switch (targetType) {
      case "de.monticore.symboltable.serialization.JsonParser":
        if (substituteJsonParserMethods(invocation, methodName)) {
          return true;
        }
        break;
      case "de.monticore.symboltable.serialization.json.JsonObject":
      case "de.monticore.symboltable.serialization.json.JsonElement":
      case "de.monticore.symboltable.serialization.json.JsonString":
      case "de.monticore.symboltable.serialization.json.JsonArray":
      case "de.monticore.symboltable.serialization.json.JsonBoolean":
      case "de.monticore.symboltable.serialization.json.JsonNull":
      case "de.monticore.symboltable.serialization.json.JsonNumber":
      case "de.monticore.symboltable.serialization.json.JsonTraceableJsonObject":
      case "de.monticore.symboltable.serialization.json.JsonUserJsonString":
        if (substituteJsonElementMethods(invocation, methodName)) {
          return true;
        }
        break;
    }

    return super.substituteMethodInvocation(invocation);
  }

  // Replace with JSON.parse
  private boolean substituteJsonParserMethods(MethodInvocationElement invocation, String methodName) {
    switch (methodName) {
      case "parseJsonObject":
      case "parse":
        this.print("JSON.parse(").printArgList(invocation.getArguments()).print(")");
        return true;
    }
    return false;
  }

  // Replace with the TS access operator "[x]"
  private boolean substituteJsonElementMethods(MethodInvocationElement invocation, String methodName) {
    switch (methodName) {
      case "isJsonObject":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print(" == \"object\")");
        return true;
      case "isJsonArray":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(Array.isArray(").print(invocation.getTargetExpression()).print("))");
        return true;
      case "isJsonBoolean":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print(" == \"boolean\")");
        return true;
      case "isJsonString":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print(" == \"string\")");
        return true;
      case "isJsonNumber":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print(" == \"number\")");
        return true;
      //          case "isJsonNull":
      case "getAsJsonObject":
      case "getAsJsonArray":
      case "getAsJsonBoolean":
      case "getAsJsonString":
      case "getAsJsonNumber":
      case "getNumberAsFloat":
      case "getNumberAsDouble":
      case "getNumberAsInteger":
      case "getNumberAsLong":
      case "getValue":
      case "getValues":
        //          case "putMember":
        //          case "removeMember":
        //          case "getMemberNames":
      case "toString":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print(invocation.getTargetExpression());
        return true;
      //          case "getMembers":
      //          case "setMembers":
      //          case "sizeMembers":
      //          case "hasMembers":
      case "hasMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(").print(invocation.getTargetExpression()).print("[")
            .printArgList(invocation.getArguments()).print("] != undefined && ")
            .print(invocation.getTargetExpression()).print("[")
            .printArgList(invocation.getArguments()).print("] != null)");
        return true;
      case "hasStringMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print("[").printArgList(
            invocation.getArguments()).print("] == \"string\")");
        return true;
      case "hasArrayMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(Array.isArray(").print(invocation.getTargetExpression()).print("[").printArgList(
            invocation.getArguments()).print("]))");
        return true;
      case "hasBooleanMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print("[").printArgList(
            invocation.getArguments()).print("] == \"boolean\")");
        return true;
      case "hasObjectMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print("[").printArgList(
            invocation.getArguments()).print("] == \"object\")");
        return true;
      case "hasIntegerMember":
      case "hasDoubleMember":
      case "hasFloatMember":
      case "hasLongMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print("(typeof ").print(invocation.getTargetExpression()).print("[").printArgList(
            invocation.getArguments()).print("] == \"number\")");
        return true;
      case "getStringMember":
      case "getStringMemberOpt":
      case "getArrayMember":
      case "getArrayMemberOpt":
      case "getBooleanMember":
      case "getBooleanMemberOpt":
      case "getObjectMember":
      case "getObjectMemberOpt":
      case "getIntegerMember":
      case "getIntegerMemberOpt":
      case "getDoubleMember":
      case "getDoubleMemberOpt":
      case "getFloatMember":
      case "getFloatMemberOpt":
      case "getLongMember":
      case "getLongMemberOpt":
      case "getMember":
        UMLPAdapterUtils.printMacroName(this, methodName);
        this.print(invocation.getTargetExpression()).print("[").printArgList(invocation.getArguments()).print("]");
        return true;
    }
    return false;
  }

  // Better overload
  @Override
  public boolean substituteNewClass(NewClassElement newClass) {
    String className = newClass.getTypeAsElement().toString();
    switch (className) {
      case "umlp.backendrte.command.SimpleResult":
        if (newClass.getArguments().size() == 3) {
          ExtendedElement argument = newClass.getArgument(1);
          if (argument.getTypeAsElement().toString().equals("umlp.backendrte.common.LoadingState")) {
            print("new SimpleResult().createFromLoadingState(").printArgList(newClass.getArguments()).print(")");
            return true;
          } else {
            print("new SimpleResult().createFromObject(").printArgList(newClass.getArguments()).print(")");
            return true;
          }
        }
      case "input.GemTextInputValidity":
      case "input.src.lib.gemtextinput.GemTextInputTypes.GemTextInputValidity":
        if (newClass.getArguments().size() == 0) {
          print("{}");
          return true;
        } else if (newClass.getArguments().size() == 2) {
          print(String.format("{status: %s, message: %s}", newClass.getArguments().get(0), newClass.getArguments().get(1)));
          return true;
        } else {
          // ERROR
          return false;
        }
      default:
    }

    return super.substituteNewClass(newClass);
  }

  @Override
  public boolean substituteTypeCast(TypeCastElement castExpression) {
    if (castExpression.getExpression().toString().equals("this")) {
      print("<").print(types().asElement(castExpression.getType()).getSimpleName().toString()).print(">");
      print("<unknown>").print(castExpression.getExpression());
      return true;
    }
    return super.substituteTypeCast(castExpression);
  }
}
