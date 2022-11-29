<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default UMLP2Java generation.

  Call it using the CLI: .. -ct umlp.UMLP2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator")}

<#-- Call all decorators -->
<#assign beDecorator = tc.instantiate("umlp.generator.decorator.BEDecorator", [glex, hwPath, true])>
${beDecorator.transform(ast, decoratedAST)}

<#assign commandDecorator = tc.instantiate("umlp.generator.decorator.commands.CommandDecorator", [glex])>
${commandDecorator.transform(ast, decoratedAST)}

<#-- TopDecorator -->
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>
${topDecorator.decoratePackage(decoratedAST)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}