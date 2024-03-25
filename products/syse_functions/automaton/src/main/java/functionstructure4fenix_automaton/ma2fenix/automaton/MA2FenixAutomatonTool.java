package functionstructure4fenix_automaton.ma2fenix.automaton;


import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import functionstructure4fenix.FunctionStructure4FenixManager;
import functionstructure4fenix.FunctionStructure4FenixManagerImpl;
import functionstructure4fenix.ma2fenix.MA2FenixTool;
import functionstructure4fenix_automaton.FunctionStructure4Fenix_AutomatonManager;
import functionstructure4fenix_automaton.FunctionStructure4Fenix_AutomatonManagerImpl;
import montiarc.MontiArcMill;

import java.util.Optional;

public class MA2FenixAutomatonTool extends MA2FenixTool {

  public static void main(String[] args) {
    Preconditions.checkNotNull(args);
    MA2FenixAutomatonTool tool = new MA2FenixAutomatonTool();
    tool.init();
    tool.run(args);
  }

  @Override
  public void generate(String main) {
    Preconditions.checkNotNull(main);
    MA2FenixAutTransformer transformer = new MA2FenixAutTransformer();
    Optional<ComponentTypeSymbol> mainType = MontiArcMill.globalScope().resolveComponentType(main);
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

