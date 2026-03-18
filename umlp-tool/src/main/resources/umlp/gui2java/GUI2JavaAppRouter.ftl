${tc.signature("pageRoutes")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="pageRoutes" type="java.util.List<umlp.generator.util.GUIPageRoute>"-->
package app;

import umlp.guiutil.Router;

public class AppRouter {
<#list pageRoutes as route>
	<#if route.routingIdCount() gt 0>
	public static void navigateTo${route.pageName()}(<#list 0..<route.routingIdCount() as i>long val_${i}<#sep>,</#sep></#list>) {
	  Router.navigateTo("${route.path()}/${route.pageName()}/"<#list 0..<route.routingIdCount() as i>+val_${i}<#sep>+"/"</#sep></#list>);
	}
	<#else>
	public static void navigateTo${route.pageName()}() {
	  Router.navigateTo("${route.path()}/${route.pageName()}");
	}
	</#if>
</#list>
}