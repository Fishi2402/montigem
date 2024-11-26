<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement"-->
${tc.signature("packageRoute", "componentRoutes", "defaultRoute")}
<#-- @ftlvariable name="packageRoute" type="umlp.generator.gui.gentypescript.config.PackageRoute"-->

<#--Add a import to the SecurityManager to all page classes-->
<#assign decorator = tc.instantiate("login_module.PackagePathDecorator", [glex]) />
<#-- @ftlvariable name="decorator" type="login_module.PackagePathDecorator"-->

<#-- Add the login modules routes to the respective Routing.ts file-->
${decorator.addRoute(packageRoute, "gui", "loginmodule")}
${info("After decorator: PackagePathDecorator", "Login module")}
