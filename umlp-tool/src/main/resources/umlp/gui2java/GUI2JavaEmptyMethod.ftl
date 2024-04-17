<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.args2ArgsString(args)>

${cd4c.method("public ${type} ${name}(${argsString})")}
