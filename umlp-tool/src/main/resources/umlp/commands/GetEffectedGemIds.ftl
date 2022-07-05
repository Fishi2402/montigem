<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname")}
${cd4c.method("public Collection<Long> getEffectedGemIds()")}

return Collections.singleton(${classname?uncap_first}.getGemId());
