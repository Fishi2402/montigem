<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "argTypes")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.types2ArgsString(argTypes)>

${cd4c.method("public ${type} ${name}(${argsString})")}
