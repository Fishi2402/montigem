<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "guiHtmlGenerator")}

<#-- Generate HTML-Code -->
${guiHtmlGenerator.generate(ast)}
