<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "keys", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp_$">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
if (this.${name} === undefined) {
  this.${name} = new Map();
}
let ${tmpVar}: ${tsType} = new ${tsType}Builder();
${tc.includeArgs("umlp.gui2java.GUI2JavaStaticBuilderInitBody", [tmpVar, name, keys])}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}