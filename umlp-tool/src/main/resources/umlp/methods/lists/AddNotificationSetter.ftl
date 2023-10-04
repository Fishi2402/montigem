<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "method")}

_${name}.add(index, element);
set${name?cap_first}Present(true);
check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifyAdd${name?cap_first}((${domain}) this, element, index, scope));
