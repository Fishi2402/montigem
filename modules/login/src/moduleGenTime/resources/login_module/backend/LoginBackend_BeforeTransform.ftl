${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}

<#assign decorator = tc.instantiate("login_module.SpringSecurityConfigDecorator", [glex]) />
${decorator.transform(ast, decoratedAST)}
${info("After decorator: LoginEndpointDecorator", "Login module")}
