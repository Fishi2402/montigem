<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "domainObjGetter", "attrName", "targetType", "params", "additionalValidationSources")}
<#assign paramString = genHelper.params2String(params)>
${cd4c.method("public ${targetType} get${methodName?cap_first}ForAngular(${paramString})")}

return this.get${methodName?cap_first}(<#list params as param>${param.getName()}<#sep>, </#list>);
