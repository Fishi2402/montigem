<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "concreteSubclassesInclSelf")}
${cd4c.method("public Promise<${name}> _do${name}Opt(long gemId)")}

let value: ${name} = null;
<#list concreteSubclassesInclSelf as subclass>
<#if subclass?index != 0>else </#if>if (this.${subclass.name?uncap_first}s.has(gemId)) {
  let tmp: ${subclass.name} = this.${subclass.name?uncap_first}s.get(gemId);
  value = tmp;
}
</#list>
<#list concreteSubclassesInclSelf as subclass>
<#if subclass.name == name>
<#if concreteSubclassesInclSelf?size != 0>else </#if> {
  return Promise.resolve(null);
}
</#if>
</#list>

return Promise.resolve(value);
