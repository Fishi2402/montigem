${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}

<#assign decorator = tc.instantiate("sovd_gateway_module.SvodGatewayDecorator", [glex]) />
${decorator.transform(ast, decoratedAST)}
${info("After generate: SovdBackendDecorator", "Sovd Gateway module")}
