package functionstructure4fenix.ma2fenix;


import arcbasis._symboltable.ArcComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.Optional;

public class MA2FenixTool extends MontiArcTool {

  public static void main(String[] args) {
    Preconditions.checkNotNull(args);
    MA2FenixTool tool = new MA2FenixTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public Options addStandardOptions(Options options) {
    Preconditions.checkNotNull(options);
    options.addOption(org.apache.commons.cli.Option.builder("main")
        .hasArgs()
        .desc("Sets the main component of the montiarc model (optional).")
        .build());
    return super.addStandardOptions(options);
  }

  @Override
  public void run(CommandLine cl) {
    Preconditions.checkNotNull(cl);

    if(cl.hasOption("main")) {
      super.run(cl);
      this.generate(cl.getOptionValue("main"));
    }else{
      Log.error("Main component not defined. Use '-main' to set the main component.");
    }
  }

  public void generate(String main) {
    Preconditions.checkNotNull(main);
    MA2FenixTransformer transformer = new MA2FenixTransformer();
    Optional<ArcComponentTypeSymbol> mainType = MontiArcMill.globalScope().resolveArcComponentType(main);
    if (mainType.isEmpty()) {
      Log.error("Main component not found in model path.");
    }
    if(mainType.get().isPresentAstNode()) {
      transformer.transform(mainType.get().getAstNode());
    }else{
      Log.error("AST node of main component is missing.");
    }
  }
}

