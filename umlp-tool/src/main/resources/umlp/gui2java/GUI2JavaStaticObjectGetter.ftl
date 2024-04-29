<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "keys")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>

${cd4c.method("public ${type} get${name?cap_first}(${argsString})")}
return this.${name}<#list keys as key>.get(${key.getName()})</#list>;