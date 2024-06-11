<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "callerExpression", "params")}
<#assign mcType = type.printType()>
<#assign eventVar = "event_$">
<#assign paramString = genHelper.params2String(mcType, eventVar, params)>

${cd4c.method("public void ${name}(${paramString})")}
if (${callerExpression} != null) {
  ${callerExpression}(${eventVar});
}