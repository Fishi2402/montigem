<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "method")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${cdPrinter.printType(method.getMCReturnType())} element = this._${name}.remove(index);
check${name?cap_first}();
${domain?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${domain}) this, element, index, scope));
return element;
