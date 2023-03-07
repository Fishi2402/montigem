<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "guiHtmlGenerator")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.genhtml.decorator.GUI2HtmlDecorator", [glex])>

${guiDecorator.transform(ast, decoratedAST)}

<#-- Generate HTML-Code -->
${guiHtmlGenerator.generate(decoratedAST)}
