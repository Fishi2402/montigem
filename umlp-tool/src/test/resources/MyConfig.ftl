<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template is just for testing
-->

${tc.signature("decoratedAST", "glex", "hwPath", "cdGenerator")}

<#-- Generate only commands -->
<#assign commandDecorator = tc.instantiate("umlp.generator.decorator.commands.CommandDecorator", [glex])>
${commandDecorator.transform(ast, decoratedAST)}

<#-- TopDecorator -->
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>
${topDecorator.decoratePackage(decoratedAST)}

<#-- Generate Java-Code -->
${cdGenerator.generate(decoratedAST)}

