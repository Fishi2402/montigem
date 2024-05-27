<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mcType", "name", "setter")}
<#assign genHelper = glex.getGlobalVar("genHelper")>
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
<#assign type = cdPrinter.printType(mcType)>

${cd4c.method("public void ${setter}(${type} val)")}
this.${name} = val;