${tc.signature("glex")}

<#--Transitive after/before templates does not work: -->
<#--${mod.addModuleToHookpoint("umlp.gui2java.InitPageBody",  "login_module.frontend.LoginFrontendPageNGOnInit")}-->
<#--Instead we use a hookpoint-->
${bindHookPoint("component.InitPageBody", tc.instantiate("de.monticore.generating.templateengine.TemplateHookPoint", ["login_module.frontend.LoginFrontendPageNGOnInit"]))}


${mod.addModuleToHookpoint("umlp.generator.hook.AfterTransformGui2C",  "login_module.frontend.LoginFrontend_AfterTransformGui2C")}
