<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2Component generation.
-->
${tc.signature("decoratedAST", "glex", "hwPath", "outPath", "componentGenerator", "viewComponents")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}


<#-- Call all decorators -->
<#assign guiDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUI2TypeScriptDecorator", [glex])>

<#-- TopDecorator -->
<#assign topDecoratorTS = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [hwPath])>
<#assign topDecoratorJava = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>

<#assign annotationDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecoratorAndStandaloneConfig", [glex, hwPath, outPath])>

<#--ToDo: Maybe use a better template name-->
${includeArgs("umlp.generator.hook.BeforeTransformGui2C", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}

${guiDecorator.transform(ast, decoratedAST, viewComponents, hwPath, outPath)}

${topDecoratorTS.decorate(decoratedAST)}
${topDecoratorJava.decorate(decoratedAST)}

${annotationDecorator.transform(decoratedAST,ast)}

${includeArgs("umlp.generator.hook.AfterTransformGui2C", decoratedAST, glex, hwPath, componentGenerator, guiDecorator, topDecorator)}


${componentGenerator.generate(decoratedAST)}
