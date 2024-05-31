<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "item", "getterExpression", "params", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("protected void init${name?cap_first}(${paramString})")}
for (${javaType} ${item} : ${getterExpression}) {
  ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
}