<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("tmpVar", "name", "keys")}

<#list keys as key>
  <#assign i = key?index - 1>
  <#if key?has_next>
    if (!this.${name}
      <#if i gt -1>
        <#list keys[0..i] as prev>
          .get(${prev.getName()})
        </#list>
      </#if>
          .has(${key.getName()})) {
  </#if>
  this.${name}
  <#if i gt -1>
    <#list keys[0..i] as prev>
      .get(${prev.getName()})
    </#list>
  </#if>
  <#if key?has_next>
      .set(${key.getName()}, new Map());
  }
  <#else>
      .set(${key.getName()}, ${tmpVar});
  </#if>
</#list>
;