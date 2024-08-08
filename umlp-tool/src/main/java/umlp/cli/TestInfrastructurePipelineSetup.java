package umlp.cli;

import umlp.cli.common.IOperation;

public class TestInfrastructurePipelineSetup {
  final IOperation<UMLPPipelineData> testInfrastructure = data -> {
    //TODO call generator?

    return true;
  };

  void createTestInfrastructure(UMLPToolPipelineSetup umlpPipeline) {
    umlpPipeline.testInfrastructure.register(testInfrastructure);
  }
}
