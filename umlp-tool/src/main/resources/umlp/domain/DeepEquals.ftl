<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "gemAssocs", "listAssocs")}
${cd4c.method("public boolean TESTING_deepEquals(Object o)")}
if(this == o){
  return true;
}

if (o instanceof ${initializationTypeName?cap_first}) {
${initializationTypeName?cap_first} other = (${initializationTypeName?cap_first}) o;

return this.equals(other) &&
  <#list gemAssocs as gemAssoc>
  get${gemAssoc.getName()?cap_first}Gem().isPresent() == other.get${gemAssoc.getName()?cap_first}Gem().isPresent() &&
  (!get${gemAssoc.getName()?cap_first}Gem().isPresent() || get${gemAssoc.getName()?cap_first}().TESTING_deepEquals(other.get${gemAssoc.getName()?cap_first}())) &&
  </#list>
  <#list listAssocs as gemListAssoc>
  _internal_get${gemListAssoc.getName()?cap_first}GemList().TESTING_deepEquals(other. _internal_get${gemListAssoc.getName()?cap_first}GemList()) &&
  </#list>
  true;
}
return false;
