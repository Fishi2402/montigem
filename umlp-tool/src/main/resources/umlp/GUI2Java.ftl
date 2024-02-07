<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Java generation.

  Call it using the CLI: .. -ct umlp.GUI2Java

-->
${tc.signature("decoratedAST", "glex", "hwPath", "guiJavaGenerator", "viewComponents")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.genjava.decorator.GUI2JavaDecorator", [glex])>

<#-- TopDecorator, which is aware about hand-written TS code -->
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>

<#--${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, guiJavaGenerator, guiDecorator, topDecorator)}-->

${guiDecorator.transform(ast, decoratedAST, viewComponents)}

${topDecorator.decorate(decoratedAST)}

<#--${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, guiJavaGenerator, guiDecorator, topDecorator)}-->

<#-- Generate Java-Code -->
${guiJavaGenerator.generate(decoratedAST)}
