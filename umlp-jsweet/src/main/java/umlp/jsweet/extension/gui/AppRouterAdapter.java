/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.gui;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.InvocationElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import umlp.jsweet.extension.util.RouterAdapterUtils;

import javax.lang.model.element.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapting generated GUI components for angular
 */
public class AppRouterAdapter extends PrinterAdapter {

  public AppRouterAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }
  
  @Override
  public boolean substituteType(TypeElement classElement) {
    if (classElement.getSimpleName().toString().equals(RouterAdapterUtils.APP_ROUTER)) {
      print("import { Injectable, inject } from '@angular/core';").println();
      print("import { Router } from '@angular/router';").println().println();
    }
    return super.substituteType(classElement);
  }
  
  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    Element enclosingElement = executable.getEnclosingElement();
    String className = enclosingElement.getSimpleName().toString();
    if (className.equals(RouterAdapterUtils.APP_ROUTER)) {
      String methodName = executable.getSimpleName().toString();
      
      if (methodName != null && methodName.startsWith(RouterAdapterUtils.APP_ROUTER_NAVIGATE_PREFIX)) {
        for (Modifier modifier : executable.getModifiers()) {
          if (modifier == Modifier.PUBLIC || modifier == Modifier.PROTECTED ||
              modifier == Modifier.PRIVATE || modifier == Modifier.STATIC) {
            print(modifier.toString()).print(" ");
          }
        }
        
        print(methodName).print("(");
        List<? extends VariableElement> parameters = executable.getParameters();
        if (!parameters.isEmpty()) {
          for (VariableElement param : parameters) {
            print(param.getSimpleName().toString() + ": ");
            print(getMappedType(param.asType()) + ", ");
          }
        }
        print(RouterAdapterUtils.ROUTER_VAR + ":" + RouterAdapterUtils.ROUTER + ")");
        
        if (!executable.getReturnType().toString().equals("void")) {
          print(": ").print(getMappedType(executable.getReturnType()));
        }
        
        Tree methodTree = util().lookupTree(context, executable);
        if (methodTree instanceof MethodTree method) {
          if (method.getBody() != null) {
            print(method.getBody());
          }
        }
        
        return true;
      }
    }
    return super.substituteExecutable(executable);
  }
  
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    String methodName = invocation.getMethodName();
    if (methodName.startsWith(RouterAdapterUtils.APP_ROUTER_NAVIGATE_PREFIX)) {
      ExtendedElement target = invocation.getTargetExpression();
      // Invoke navigateTo inside of AppRouter class
      if (target != null && target.toString().equals(RouterAdapterUtils.ROUTER)) {
        print(RouterAdapterUtils.ROUTER_VAR + "." + RouterAdapterUtils.NAVIGATE + "([");
        if (invocation.getArguments() != null && !invocation.getArguments().isEmpty()) {
          String argNames = invocation.getArguments().stream()
              .map(Object::toString)
              .collect(Collectors.joining(", "));
          print(argNames);
        }
        print("])");
        return true;
      }
      // Invoke navigateToX from AppRouter
      else if (target != null && target.toString().equals(RouterAdapterUtils.APP_ROUTER)) {
        print(RouterAdapterUtils.APP_ROUTER + "." + invocation.getMethodName() + "(");
        if (invocation.getArguments() != null && !invocation.getArguments().isEmpty()) {
          for (ExtendedElement arg : invocation.getArguments()) {
            print(arg + ", ");
          }
        }
        print("this." + RouterAdapterUtils.ROUTER_GLOBAL_VAR + ")");
        return true;
      }
    }
    
    return super.substituteMethodInvocation(invocation);
  }
}
