package login_module;

import umlp.cli.UMLPFilePipeline;
import umlp.cli.common.Pipeline;

/**
 * This operation can be added via the module system by adding oneself to the
 * <pre>umlp.UMLPToolInit</pre> hookpoint and adding it at the start of the pipeline:
 * <code>${mainPipelineSetup.umlpFilePipelineSetup.astPipe.registerFirst(tc.instantiate("login_module.TestOperation"))}</code>
 */
@SuppressWarnings("unused")
public class TestOperation extends Pipeline<UMLPFilePipeline.Data> {
  @Override
  public boolean doWork(UMLPFilePipeline.Data data) {
    System.err.println("[TestOperation] data#ast called " + data.getAST());
    return true;
  }
}
