<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "listItems", "keys", "initList")}
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
const ${tmpVar}: ${type} = [];
<#list listItems as item>
${tmpVar}.push(${item});
</#list>

if (this.${name} == null) {
  this.${name} = new Map();
}
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
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}