${tc.signature("prefix", "path", "pageName", "arguments", "hasBody")}
<#-- @ftlvariable name="prefix" type="java.lang.String"-->
<#-- @ftlvariable name="path" type="java.lang.String"-->
<#-- @ftlvariable name="pageName" type="java.lang.String"-->
<#-- @ftlvariable name="arguments" type="java.lang.String"-->
<#-- @ftlvariable name="hasBody" type="java.lang.Boolean"-->
<#assign pathElements = path?split("[/\\\\]", "r")>
${prefix}navigateTo<#list pathElements as el>${el?cap_first}</#list>${pageName}(${arguments})<#if !hasBody>;</#if>