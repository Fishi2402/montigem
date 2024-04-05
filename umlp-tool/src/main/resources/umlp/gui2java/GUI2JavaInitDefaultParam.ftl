<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "valueExpression")}
${cd4c.method("protected void init${name?cap_first}()")}
<#if valueExpression.isPresent()>this.${name?cap_first}(${valueExpression.get()});</#if>