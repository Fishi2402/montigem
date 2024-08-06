<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "method")}

boolean res = _${name}.add(element);
set${name?cap_first}Present(true);
return res;
