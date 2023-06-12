<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrNameList")}
${cd4c.method("public void buildJson(${command} command, any printer)")}

super.buildJson(command, printer);
<#list attrNameList as attrName>
  printer["${attrName}"] = command.get${attrName?cap_first}();
</#list>
