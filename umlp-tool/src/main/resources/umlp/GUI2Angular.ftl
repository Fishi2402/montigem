<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Component generation.
-->
${tc.signature("decoratedAST", "glex", "hwPath", "outPath", "generator")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}

<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUI2AngularClassDecorator", [glex])>
<#assign topDecoratorTS = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>
<#assign annotationDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecoratorAndStandaloneConfig", [glex, hwPath, outPath])>

<#--ToDo: Maybe use a better template name-->
${includeArgs("umlp.generator.hook.BeforeTransformGui2C", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}

${guiDecorator.transform(ast, decoratedAST, hwPath, outPath)}

${topDecoratorTS.decorate(decoratedAST)}
<#--No need to call the topDecoratorJava, as the topDecoratorTS extends the default one-->

${annotationDecorator.transform(decoratedAST,ast)}

${includeArgs("umlp.generator.hook.AfterTransformGui2C", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}


${componentGenerator.generate(decoratedAST)}
