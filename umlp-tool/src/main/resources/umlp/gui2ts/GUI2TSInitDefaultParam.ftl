<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "valueExpression", "init", "getter", "setter", "initList")}

${cd4c.method("protected void ${init}()")}
if (this.${getter}() === undefined) {
  <#if valueExpression.isPresent()>this.${setter}(${valueExpression.get()});</#if>
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}