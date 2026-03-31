<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nameAlias", "path", "pageName")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#assign pathElements = path?split("/")>
<#assign prefix = "AppRouter.">

${cd4c.method("public Consumer<Void> ${nameAlias}()")}
return $ -> {
	${tc.includeArgs("umlp.gui2java.predefined.NavigateToMethodName", [prefix, path, pageName, "", false])}
};
