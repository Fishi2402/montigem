<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "guiJavaGenerator")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.genjava.decorator.GUI2JavaDecorator", [glex])>

<#-- TopDecorator -->
<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>

<#--${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, guiJavaGenerator, guiDecorator, topDecorator)}-->

${guiDecorator.transform(ast, decoratedAST)}

${topDecorator.decorate(decoratedAST)}

<#--${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, guiJavaGenerator, guiDecorator, topDecorator)}-->

<#-- Generate Java-Code -->
${guiJavaGenerator.generate(decoratedAST)}
