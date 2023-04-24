<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}

${name} value = null;
<#list concreteSubclassesInclSelf as subclass>
<#if subclass?index != 0>else </#if>if (${subclass.name?uncap_first}s.containsKey(gemId)) {
  ${subclass.name} tmp =  ${subclass.name?uncap_first}s.get(gemId);
  <#if !(subclass.modifier.isAbstract() || subclass.symbol.isIsInterface()) >
  if (!tmp.containsObserver(the${subclass.name}CommandObserver)) {
    tmp.addObserver(the${subclass.name}CommandObserver);
  }
  </#if>
  value = tmp;
}
</#list>
<#list concreteSubclassesInclSelf as subclass>
<#if subclass.name == name>
<#if concreteSubclassesInclSelf?size != 0>else </#if> if (dummyProvider != null) {
  value = dummyProvider.invent${name}();
} else {
  return Optional.empty();
}
</#if>
</#list>

return Optional.ofNullable(value);
