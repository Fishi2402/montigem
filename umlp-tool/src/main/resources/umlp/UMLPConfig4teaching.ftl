<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default UMLP2Java generation.

  Call it using the CLI: .. -ct umlp.UMLP2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator")}

<#-- Call all decorators -->
<#assign beDecorator = tc.instantiate("umlp.generator.domain.decorator.BEDecorator", [glex, hwPath])>
${beDecorator.setShellApi(true)}
<#assign commandDecorator = tc.instantiate("umlp.generator.domain.decorator.commands.CommandDecorator", [glex])>
<#assign requestDecorator = tc.instantiate("umlp.generator.domain.decorator.requests.GetFreeGemIdRequestDecorator", [glex])>
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>

${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

${beDecorator.transform(ast, decoratedAST)}
${commandDecorator.transform(ast, decoratedAST)}
${requestDecorator.transform(ast, decoratedAST)}
${topDecorator.decorate(decoratedAST)}

<#-- Generate GetAll Rest Service for teaching -->
<#assign restGetAllGenerator = tc.instantiate("umlp.generator.service.RestGetAllDecorator", [glex])>
${restGetAllGenerator.transform(ast, decoratedAST)}

${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, cdGenerator, schemaGenerator, beDecorator, commandDecorator, requestDecorator, topDecorator)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}

<#-- Generate SQL-Schema -->
${schemaGenerator.generate(ast)}

<#-- Generate GUI for teaching -->
<#assign uiGenerator = tc.instantiate("umlp.generator.gui.UI4TeachingGenerator", [tc.getGeneratorSetup()])>
${uiGenerator.generate(ast)}