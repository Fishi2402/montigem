<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("keys")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

true
<#list keys as key>
  <#assign i = key?index - 1>
  && this.${name}
  <#if i gt -1>
    <#list keys[0..i] as prev>
      .get(${prev.getName()})
    </#list>
  <#else>
      .has(${key.getName()})
  </#if>
</#list>
