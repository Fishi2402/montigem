${signature("decoratedAST", "glex", "hwPath", "componentGenerator", "guiDecorator", "topDecorator")}

<#--Add a import to the SecurityManager to all page classes-->
<#assign decorator = tc.instantiate("login_module.SecurityManagerImportDecorator", [glex]) />
${decorator.transform(ast, decoratedAST)}
${info("After decorator: SecurityManagerImportDecorator", "Login module")}
