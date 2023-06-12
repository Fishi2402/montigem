<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Component generation.
-->
${tc.signature("decoratedAST", "glex", "hwPath", "outPath", "componentGenerator", "viewComponents")}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUI2TypeScriptDecorator", [glex])>

<#-- TopDecorator -->
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>

<#assign annotationDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecorator", [glex, hwPath, outPath])>

<#--${includeArgs("umlp.generator.hook.BeforeTransform", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}-->

${guiDecorator.transform(ast, decoratedAST, viewComponents)}

${topDecorator.decorate(decoratedAST)}

${annotationDecorator.transform(decoratedAST)}

<#--${includeArgs("umlp.generator.hook.AfterTransform", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}-->

<#-- Generate Java-Code -->
${componentGenerator.generate(decoratedAST)}
