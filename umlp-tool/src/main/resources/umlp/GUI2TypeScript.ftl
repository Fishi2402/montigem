<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template does the default GUI2TypeScript generation.
-->
${tc.signature("decoratedAST", "glex", "hwPath", "outPath", "importsPath", "generator")}

<#-- Initialize all modules, adding their templates to the other hookpoints, e.g., BeforeTransform -->
${includeArgs("umlp.generator.hook.InitModules", glex)}

<#-- Decorators -->
<#assign angularDecorator = tc.instantiate("umlp.generator.gui.gentypescript.decorator.GUI2AngularClassDecorator", [glex, hwPath, outPath, importsPath])>
<#assign annotationDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.GUIComponentAnnotationDecoratorAndStandaloneConfig", [glex, hwPath, outPath, importsPath])>
<#assign topDecorator = tc.instantiate("umlp.generator.gui.gentypescript.java2ts.TSTopDecorator", [glex, hwPath, outPath])>

<#--ToDo: Maybe use a better template name-->
${includeArgs("umlp.generator.hook.BeforeTransformGui2C", decoratedAST, glex, hwPath, generator, angularDecorator, topDecorator)}

${angularDecorator.transform(ast, decoratedAST)}

${topDecorator.decorate(decoratedAST)}

${annotationDecorator.transform(decoratedAST, ast)}

${includeArgs("umlp.generator.hook.AfterTransformGui2C", decoratedAST, glex, hwPath, generator, angularDecorator, topDecorator)}

${generator.generate(decoratedAST)}
