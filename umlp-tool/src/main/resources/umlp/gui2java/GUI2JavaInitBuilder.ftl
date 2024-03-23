<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "manager", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(args)>

${cd4c.method("protected void init${attr.getName()?cap_first}(${argsString})")}
this.${attr.getName()} = ${manager}.${attr.getMCType().printType()?uncap_first}Builder();
