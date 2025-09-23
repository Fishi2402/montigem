<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "hasSuperClass")}
${cd4c.method("public void addObserver(${name}Observer o)")}

Log.debug("Adding observer "+o.getClass().getName() + " on ${name} with GemId "+this.getGemId(), this.getClass().getName());
<#if hasSuperClass>
  super.addObserver(o);
</#if>
  ${name?uncap_first}Observers.add(o);
