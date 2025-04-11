package umlp.cli;

import de.monticore.prettyprint.IndentPrinter;
import umlp.cli.common.IOperation;
import umlp.cli.common.Pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

/**
 * Prints a Pipeline as a mermaid state diagram.
 * The diagram is currently "okay", inner pipelines have to be manually registered via
 * {@link IOperation#withDependantPipelines(IOperation, List)}, etc.
 *
 */
public class PipelineReporter {
  IndentPrinter printer = new IndentPrinter();
  public String report(Pipeline<?> pipeline, File file) {

    printer.indent();

    do_report(pipeline);

    printer.unindent();

    String ret = "```mermaid\n---\n" + "title: UMLP Pipeline reporter\n" + "---\n" + "stateDiagram-v2\n" +
        printer.getContent() + "\n```\n";

    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    } try {
      Files.writeString(file.toPath(), ret, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ret;
  }

  protected void do_report(Pipeline<?> pipeline) {
    String prev = "[*]";
    for (var step : pipeline.getSteps()) {
      String id = UUID.randomUUID().toString().replaceAll("-", "");
      printer.println("state \"" + step.getName().replace('<', '(').replace('>', ')') + "\" as " + id + " ");

      if (step.getValue() instanceof Pipeline) {
        printer.println("state " + id + " { ");

        printer.indent();
        do_report((Pipeline<?>) step.getValue());
        printer.unindent();

        printer.println("}");
      } else if (step.getValue() instanceof IOperation.IOperationWithDependantPipelines) {
        // We should really consider creating these reports using source code analyses?
        printer.println("state " + id + " { ");
        printer.indent();
        for (var p : ((IOperation.IOperationWithDependantPipelines<?>) step.getValue()).innerPipelinesForReporting())
          do_report(p);
        printer.unindent();

        printer.println("}");
      }

      printer.println();

      printer.println(prev + " --> " + id);
      prev = id;

    }
    for (var finallyStep : pipeline.getFinallySteps()) {
      String id = UUID.randomUUID().toString().replaceAll("-", "");
      printer.println("state \"finallyStep\" as " + id + " ");
      printer.println(prev + " --> " + id);
    }
  }


}
