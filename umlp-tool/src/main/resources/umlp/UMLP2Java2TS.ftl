<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default UMLP2Java generation.

  Call it using the CLI: .. -ct umlp.UMLP2Java2TS

-->
${tc.signature("decoratedAST", "glex", "hwPath", "outPath", "cdGenerator", "schemaGenerator")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}

<#-- Call all decorators -->
<#assign beDecorator = tc.instantiate("umlp.generator.gui.gentypescript.domain.FEDecorator", [glex, hwPath, outPath])>
<#assign commandDecorator = tc.instantiate("umlp.generator.gui.gentypescript.commands.TSCommandDecorator", [glex, hwPath, outPath])>
<#assign requestDecorator = tc.instantiate("umlp.generator.gui.gentypescript.commands.TSGetFreeGemIdRequestDecorator", [glex, hwPath, outPath])>
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [glex, hwPath, outPath])>

${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

${beDecorator.transform(ast, decoratedAST)}
${commandDecorator.transform(ast, decoratedAST)}
${requestDecorator.transform(ast, decoratedAST)}
${topDecorator.decorate(decoratedAST, true)}

${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}
