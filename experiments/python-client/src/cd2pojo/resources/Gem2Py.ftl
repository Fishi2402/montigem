<#--CD2OwnDecorator.ftl-->
${tc.signature("glex", "deConf", "setup")}
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="deConf" type="de.monticore.cd.codegen.DecoratorConfig" -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="setup" type="de.monticore.generating.GeneratorSetup" -->

${setup.setTracing(false)}
${setup.setCommentStart("#")}
${setup.setCommentEnd("\n")}

<#--Apply the default creator: Copy the original CD and use it as the base-->
${deConf.withCopyCreator().defaultApply()}

<#--It is possible to add your own decorators via this config template too: -->
<#-- We just have to add the class to the classpath of the CDTool -->
<#--${deConf.withDecorator("mc.MyOwnDecorator").defaultApply()}-->
${deConf.withGetters().ignoreOnName("noGetter").defaultApply()}

${deConf.withDecorator("umlp.py.GemBaseDecorator").applyOnName("pygem")}

${glex.replaceTemplate("cd2java.Class", glex.templateHP("cd2python.Class"))}
