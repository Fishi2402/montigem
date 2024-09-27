${tc.signature("packageComponentRoutings", "defaultRouting", "subRoutes")}

<#--Add a import to the SecurityManager to all page classes-->
<#assign decorator = tc.instantiate("login_module.SecurityManagerImportDecorator", [glex]) />
${decorator.transform(ast, subRoutes)}
${info("After decorator: SecurityManagerImportDecorator", "Login module")}
