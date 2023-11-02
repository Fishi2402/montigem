${tc.signature("glex")}

${glex.addAfterTemplate("umlp.generator.hook.BeforeTransform",  tc.instantiate("de.monticore.generating.templateengine.TemplateHookPoint", ["some_module.Backend_BeforeTransform"]))}

${glex.addAfterTemplate("umlp.generator.hook.AfterTransform",  tc.instantiate("de.monticore.generating.templateengine.TemplateHookPoint", ["some_module.Backend_AfterTransform"]))}
