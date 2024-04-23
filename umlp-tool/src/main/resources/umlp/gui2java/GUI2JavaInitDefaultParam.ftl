<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "valueExpression", "init", "setter")}
${cd4c.method("protected void ${init}()")}
<#if valueExpression.isPresent()>this.${setter}(${valueExpression.get()});</#if>