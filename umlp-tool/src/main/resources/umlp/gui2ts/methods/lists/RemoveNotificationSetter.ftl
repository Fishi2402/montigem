<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain", "name", "method")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

let element:${cdPrinter.printType(method.getMCReturnType())} = this._${name}[index];
this._${name}.splice(index, 1);
this.check${name?cap_first}();
this.${domain?uncap_first}Observers.forEach(obs => obs.maybeNotifyRemove${name?cap_first}(<${domain}> <any> this, element, index, scope));
return element;
