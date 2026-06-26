<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "setter", "setExternally", "notifyObservers")}
<#assign mcType = type.printType()>

${cd4c.method("public void ${setter}(${mcType} val)")}
this.${setExternally} = true;
this.${name} = val;

<#if notifyObservers??>
  this.${notifyObservers}();
</#if>
