${tc.signature("glex")}

<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement"-->
<#-- @ftlvariable name="mod" type="umlp.ModuleController"-->


${mod.addModuleToHookpoint("umlp.generator.hook.BeforeTransform",  "login_module.backend.LoginBackend_BeforeTransform")}
