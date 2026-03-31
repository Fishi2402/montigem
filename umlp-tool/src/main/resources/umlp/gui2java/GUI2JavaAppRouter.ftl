${tc.signature("pageRoutes")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="pageRoutes" type="java.util.List<umlp.generator.util.GUIPageRoute>"-->
<#assign prefix = "public static void ">
package app;

import umlp.guiutil.Router;

public class AppRouter {
<#list pageRoutes as route>
	<#if route.routingIdCount() gt 0>
		<#assign arguments>
      <#list 0..<route.routingIdCount() as i>long val_${i}<#sep>,</#sep></#list>
		</#assign>
	${tc.includeArgs("umlp.gui2java.predefined.NavigateToMethodName", [prefix, route.path(), route.pageName(), arguments, true])}
	{
	  Router.navigateTo("${route.path()}/${route.pageName()}/"<#list 0..<route.routingIdCount() as i>+val_${i}<#sep>+"/"</#sep></#list>);
	}
	<#else>
	${tc.includeArgs("umlp.gui2java.predefined.NavigateToMethodName", [prefix, route.path(), route.pageName(), "", true])}
	{
	  Router.navigateTo("${route.path()}/${route.pageName()}");
	}
	</#if>
</#list>
}