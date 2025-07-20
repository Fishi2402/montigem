<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template is called when

  Use hookpoints to hook yourself to this template

-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="data" type="umlp.cli.UMLPPipelineData"-->
<#-- @ftlvariable name="mainPipelineSetup" type="umlp.cli.UMLPToolPipeline"-->

<#--
Given a class:
public class TestOperation extends Pipeline<UMLPFilePipeline.Data> {
  @Override
  public boolean doWork(UMLPFilePipeline.Data data) {
    System.err.println("[TestOperation] data#ast called " + data.getAST());
    return true;
  }
}

The following template could be hooked to "umlp.UMLPToolInit" to add the TestOperation at the start of the pipeline

<#if mainPipelineSetup.getUmlpFilePipeline().isPresent()>
  ${mainPipelineSetup.getUmlpFilePipeline().get().getAstPipe().registerFirst(transformation)}
<#else>
  ${mainPipelineSetup.warn("0xXXXXX Warning msg")}
</#if>


-->
