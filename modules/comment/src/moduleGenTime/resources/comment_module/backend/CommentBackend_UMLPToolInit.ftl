${tc.signature("tool")}

<#-- @ftlvariable name="tool" type="umlp.UMLPTool"-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="data" type="umlp.cli.UMLPPipelineData"-->
<#-- @ftlvariable name="mainPipelineSetup" type="umlp.cli.UMLPToolPipelineSetup"-->

<#assign transformation = tc.instantiate("comment_module.UMLPClassCommentTransformation") />
${mainPipelineSetup.umlpFilePipelineSetup.astPipe.registerFirst(transformation)}