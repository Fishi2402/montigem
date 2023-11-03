${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}

<#assign someDecorator = tc.instantiate("pack_decorator.SomeDecorator", [glex]) />
${someDecorator.transform(ast, decoratedAST)}
