<#--${tc.signature("packageComponentRoutings", "defaultRouting", "subRoutes")}-->
${tc.signature("packageRoute", "componentRoutes", "defaultRoute")}

<#--Add a import to the SecurityManager to all page classes-->
<#assign decorator = tc.instantiate("login_module.PackagePathDecorator", [glex]) />
${decorator.addRoute(packageRoute, "gui", "loginmodule")}
${info("After decorator: PackagePathDecorator", "Login module")}
