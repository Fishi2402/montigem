<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "manager", "keys", "initList")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign argsString = genHelper.variableSymbols2ArgsString(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("protected void init${name?cap_first}(${argsString})")}
if (this.${name} == null) {
  ${manager}.${type?uncap_first}Builder().then((${tmpVar}: ${cdPrinter.printType(mcType)}) => {
    this.${name} = ${tmpVar};
  });
}
