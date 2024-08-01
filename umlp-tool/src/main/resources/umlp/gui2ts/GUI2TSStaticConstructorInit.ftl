<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "args", "keys", "initList")}
<#assign tsType = cdPrinter.printType(type)>
<#assign paramString = genHelper.params2String(keys)>
<#assign tmpVar = "tmp_$">

${cd4c.method("public void init${name?cap_first}(${paramString})")}
const ${tmpVar}: ${tsType} = new ${tsType}(<#list args as arg>${arg}<#sep>, </#list>);

if (this.${name} === undefined) {
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