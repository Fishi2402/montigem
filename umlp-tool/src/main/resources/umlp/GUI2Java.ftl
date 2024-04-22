<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java
-->
${tc.signature("decoratedAST", "glex", "hwPath", "generator")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.genjava.decorator.GUI2JavaClassDecorator", [glex])>

<#-- TopDecorator, which is aware about hand-written TS code -->
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>

${guiDecorator.transform(ast, decoratedAST)}

${topDecorator.decorate(decoratedAST)}

<#-- Generate Java code -->
${generator.generate(decoratedAST)}
