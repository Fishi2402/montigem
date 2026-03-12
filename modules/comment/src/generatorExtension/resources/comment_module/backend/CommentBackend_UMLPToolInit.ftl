<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="data" type="umlp.cli.UMLPPipelineData"-->
<#-- @ftlvariable name="mainPipelineSetup" type="umlp.cli.UMLPToolPipeline"-->

<#assign transformation = tc.instantiate("comment_module.UMLPClassCommentTransformation")>
<#if mainPipelineSetup.getUmlpFilePipeline().isPresent()>
  ${mainPipelineSetup.getUmlpFilePipeline().get().getAstPipe().registerFirst(transformation)}
<#else>
  ${mainPipelineSetup.warn("0x0002B Comment module was called but not executed"
    + " due to missing operation for UMLP file transformation at the generation time")}
</#if>