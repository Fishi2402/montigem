<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "valueExpression", "init", "setExternally", "wait", "getter", "setter", "initList")}

${cd4c.method("protected void ${init}()")}
<#-- if (!this.${setExternally}) { -->
  <#if valueExpression.isPresent()>this.${setter}(${valueExpression.get()});</#if>
<#-- }
else {
  firstValueFrom(this.${wait}).then(() => {
-->
    ${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
<#--  });
}
-->