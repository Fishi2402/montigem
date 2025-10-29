<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "inType", "arg", "conversion")}
<#assign inMCType = inType.printType()>

${cd4c.method("public ${mcType} convert${name?cap_first}(${inMCType} ${arg})")}
return ${conversion};