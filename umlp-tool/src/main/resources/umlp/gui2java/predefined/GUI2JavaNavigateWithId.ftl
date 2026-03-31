<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nameAlias", "path", "pageName", "types")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="types" type="java.util.List<de.monticore.types.mcbasictypes._ast.ASTMCType>"-->
<#-- @ftlvariable name="cd4c" type="de.monticore.cd.methodtemplates.CD4C"-->
<#-- @ftlvariable name="nameAlias" type="java.lang.String"-->
<#-- @ftlvariable name="path" type="java.lang.String"-->
<#-- @ftlvariable name="pageName" type="java.lang.String"-->
<#assign param = "val_">
<#assign mcTypes>
	<#list types as type>
    ${type.printType()} ${param}${type?counter}<#sep>, </#sep>
	</#list>
</#assign>
<#assign pathElements = path?split("/")>
<#assign prefix = "AppRouter.">
<#assign arguments>
  <#list types as type>${param}${type?counter}.getGemId()<#sep>, </#sep></#list>
</#assign>

${cd4c.method("public Consumer<Void> ${nameAlias}(${mcTypes})")}
return $ -> {
  ${tc.includeArgs("umlp.gui2java.predefined.NavigateToMethodName", [prefix, path, pageName, arguments, false])}
};

