<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasSuperClass")}
${cd4c.method("public void addObserver(${name}Observer o)")}

<#if hasSuperClass>
  super.addObserver(o);
</#if>
  ${name?uncap_first}Observers.add(o);




