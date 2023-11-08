${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator", "beDecorator", "commandDecorator", "requestDecorator", "topDecorator")}

<#assign generator = tc.instantiate("pack_generator.SomeGenerator") />
${generator.generate("./build/HelloWorld.txt")}
