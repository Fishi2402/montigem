<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "setExternally", "externInit", "isAbstract", "setter", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
if (!this.${setExternally}) {
  if (this.${id} != null) {
    this.${setter}(${manager}.get${javaType}Sure(this.${id}));
  }
  <#if !isAbstract>
  else {
    this.${setter}(${manager}.${javaType?uncap_first}Builder());
  }
  </#if>
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
