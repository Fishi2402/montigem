<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default RTE2Java generation.

  Call it using the CLI: .. -ct umlp.UMLP2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}

<#-- Call all decorators -->
<#assign rteDecorator = tc.instantiate("umlp.generator.rte.data.RTEDataDecorator", [glex, hwPath])>
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>

${rteDecorator.transform(ast, decoratedAST)}
${topDecorator.decorate(decoratedAST)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}
