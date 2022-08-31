<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "guiJavaGenerator")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.decorator.GUIDecorator", [glex])>
${guiDecorator.transform(ast, decoratedAST)}

<#-- Generate Java-Code -->
${guiJavaGenerator.generate(decoratedAST)}

