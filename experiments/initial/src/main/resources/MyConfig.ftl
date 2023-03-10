<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template is just for testing
-->
${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator", "schemaGenerator")}

<#-- Call all decorators -->
<#assign beDecorator = tc.instantiate("umlp.generator.domain.decorator.BEDecorator", [glex, hwPath])>
${beDecorator.transform(ast, decoratedAST)}

<#assign commandDecorator = tc.instantiate("umlp.generator.domain.decorator.commands.CommandDecorator", [glex])>
${commandDecorator.transform(ast, decoratedAST)}

<#-- TopDecorator -->
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>
${topDecorator.decorate(decoratedAST)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}

