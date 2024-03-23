<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr", "listItems", "args")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign argsString = genHelper.variableSymbols2ArgsString(args)>

${cd4c.method("protected void init${attr.getName()?cap_first}(${argsString})")}
this.${attr.getName()} = List.of(
  <#list listItems as item>${item}<#sep>,</#list>
);