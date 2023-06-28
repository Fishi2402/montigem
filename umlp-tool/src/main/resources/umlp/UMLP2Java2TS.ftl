<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default UMLP2Java generation.

  Call it using the CLI: .. -ct umlp.UMLP2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator")}

<#-- Call all decorators -->
<#assign beDecorator = tc.instantiate("umlp.generator.gui.gentypescript.domain.FEDecorator", [glex, hwPath])>
<#assign commandDecorator = tc.instantiate("umlp.generator.gui.gentypescript.commands.TSCommandDecorator", [glex])>
<#assign requestDecorator = tc.instantiate("umlp.generator.gui.gentypescript.requests.TSGetFreeGemIdRequestDecorator", [glex])>
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>

${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

${beDecorator.transform(ast, decoratedAST)}
${commandDecorator.transform(ast, decoratedAST)}
${requestDecorator.transform(ast, decoratedAST)}
${topDecorator.decorate(decoratedAST, true)}

${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}
