<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "nullCheck", "getterExpression", "params", "toArray")}
<#assign mcType = type.printType()>
<#assign paramString = genHelper.params2String(params)>

${cd4c.method("private ${mcType} get${name?cap_first}ForAngular(${paramString})")}
<#if toArray>
return GemListUtil.toArraySync(this.get${name?cap_first}(<#list params as param>${param.getName()}<#sep>, </#list>));
<#else>
return this.get${name?cap_first}(<#list params as param>${param.getName()}<#sep>, </#list>);
</#if>


