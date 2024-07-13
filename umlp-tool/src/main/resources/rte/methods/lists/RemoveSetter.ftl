<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "method")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${cdPrinter.printType(method.getMCReturnType())} element = this._${name}.remove(index);
return element;
