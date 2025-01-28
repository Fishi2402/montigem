<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "keys", "initList")}
<#assign javaType = cdPrinter.printType(type)>
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp_$">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
${javaType} ${tmpVar} = ${manager}.${javaType?uncap_first}Builder();
${tc.includeArgs("umlp.gui2java.GUI2JavaStaticBuilderInitBody", [tmpVar, name, keys])}
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}