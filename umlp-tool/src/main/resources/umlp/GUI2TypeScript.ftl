<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2TypeScript generation.
-->
${tc.signature("decoratedAST", "glex", "hwPath", "generator")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.gentypescript.decorator.GUI2TSClassDecorator", [glex])>

<#-- TopDecorator, which is aware about hand-written TS code -->
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>

${guiDecorator.transform(ast, decoratedAST)}

${topDecorator.decorate(decoratedAST)}

<#-- Generate TypeScript code -->
${generator.generate(decoratedAST)}
