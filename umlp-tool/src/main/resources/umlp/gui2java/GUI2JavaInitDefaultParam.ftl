<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "valueExpression", "init", "setter", "initList")}
${cd4c.method("protected void ${init}()")}
<#if valueExpression.isPresent()>this.${setter}(${valueExpression.get()});</#if>