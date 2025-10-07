${tc.signature("glex")}

<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement"-->
<#-- @ftlvariable name="mod" type="umlp.ModuleController"-->

${mod.addModuleToHookpoint("umlp.UMLPToolInit",  "comment_module.backend.CommentBackend_UMLPToolInit")}
${mod.addModuleToHookpoint("umlp.generator.hook.AfterTransform",  "comment_module.backend.CommentBackend_AfterTransform")}
