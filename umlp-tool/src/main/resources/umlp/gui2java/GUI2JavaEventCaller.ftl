<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "callerExpression", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.args2ArgsString(args)>

${cd4c.method("public void ${name}(${argsString})")}
${callerExpression}();
