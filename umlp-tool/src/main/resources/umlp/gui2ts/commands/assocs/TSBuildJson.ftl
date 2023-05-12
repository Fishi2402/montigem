<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrNameList")}
${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}

super.buildJson(command, jsonPrinter);
<#list attrNameList as attrName>
  jsonPrinter.member("${attrName}", command.get${attrName?cap_first}());
</#list>
