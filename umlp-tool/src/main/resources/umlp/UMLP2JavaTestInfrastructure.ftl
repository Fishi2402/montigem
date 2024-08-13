<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default UMLP2JavaTestInfrastructure generation.

  Call it using the CLI: .. -ct umlp.UMLP2JavaTestInfrastructure

-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}

<#-- Call all decorators -->
<#assign testInfrastructureDecorator = tc.instantiate("umlp.generator.test_infrastructure.domain.decorator.TestInfrastructureDecorator", [glex, hwPath])>
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>

${includeArgs("umlp.generator.hook.BeforeTransformTestInfrastructure", decoratedAST, glex, hwPath, testInfrastructureDecorator, topDecorator)}

${testInfrastructureDecorator.transform(ast, decoratedAST)}
${topDecorator.decorate(ast)}

${includeArgs("umlp.generator.hook.AfterTransformTestInfrastructure", decoratedAST, glex, hwPath, testInfrastructureDecorator, topDecorator)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}
