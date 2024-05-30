<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "keys")}
<#assign paramString = genHelper.params2String(keys)>

${cd4c.method("public ${type} get${name?cap_first}(${paramString})")}
if (this.${name} == null
  <#list keys as key>
    <#assign i = key?index - 1>
    || !this.${name}
    <#if i gt -1>
      <#list keys[0..i] as prev>
        .get(${prev.getName()})
      </#list>
    </#if>
        .has(${key.getName()})
  </#list>
) {
  return null;
}
return this.${name}<#list keys as key>.get(${key.getName()})</#list>;