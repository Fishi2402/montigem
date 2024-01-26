${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}

<#assign generator = tc.instantiate("sovd_gateway_module.SvodGatewayGenerator", [tc.getGeneratorSetup()]) />
${generator.generate(decoratedAST)}
${info("After generate: SovdBackendGenerator", "Sovd Gateway module")}
