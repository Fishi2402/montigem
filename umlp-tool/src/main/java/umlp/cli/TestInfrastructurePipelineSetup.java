package umlp.cli;

import umlp.cli.common.IOperation;

@Deprecated(forRemoval = true)
public class TestInfrastructurePipelineSetup {
  final IOperation<UMLPPipelineData> testInfrastructure = data -> {
    //TODO call generator? Nope -> We don't need this setup - remove it.

    return true;
  };

  void createTestInfrastructure(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.testInfrastructure.register(testInfrastructure);
  }
}
