<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  This config template is called when

  Use hookpoints to hook yourself to this template

-->
${tc.signature("tool")}

<#-- @ftlvariable name="tool" type="umlp.UMLPTool"-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController"-->
<#-- @ftlvariable name="data" type="umlp.cli.UMLPPipelineData"-->
<#-- @ftlvariable name="mainPipelineSetup" type="umlp.cli.UMLPToolPipelineSetup"-->

<#--
Given a class:
public class TestOperation implements IOperation<UMLPFilePipelineSetup.Data> {
  @Override
  public boolean doWork(UMLPFilePipelineSetup.Data data) {
    System.err.println("[TestOperation] data#ast called " + data.getAST());
    return true;
  }
}

The following template could be hooked to "umpl.UMLPToolInit" to add the TestOperation at the start of the pipeline

${mainPipelineSetup.umlpFilePipelineSetup.astPipe.registerFirst(tc.instantiate("myModule.TestOperation"))}


-->
