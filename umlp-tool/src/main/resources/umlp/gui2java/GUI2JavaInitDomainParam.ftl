<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "id", "init", "setExternally", "wait", "isAbstract", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
if (!this.${setExternally}) {
  if (this.${id} != null) {
    this.${name} = ${manager}.get${javaType}Sure(this.${id});
  }
  <#if !isAbstract>
  else {
    this.${name} = ${manager}.${javaType?uncap_first}Builder();
  }
  </#if>
}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}
