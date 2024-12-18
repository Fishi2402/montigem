package bin;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sd4development.SD4DevelopmentMill;
import de.monticore.lang.sd4development.SD4DevelopmentTool;
import de.monticore.lang.sd4development._symboltable.ISD4DevelopmentGlobalScope;

import java.nio.file.Path;
import java.util.List;

public class SDParserExperiment {
  public static void main(String[] args) {
    //init tool
    SD4DevelopmentMill.init();
    var tool = new SD4DevelopmentTool();

    //parse model
    var model = tool.parse("examples/testing-with-sd/test-models/TotalTest.sd");

    //prepare global scope with symbols
    ISD4DevelopmentGlobalScope globalScope = SD4DevelopmentMill.globalScope();
    var symbolPath = new MCPath(List.of(Path.of("examples/testing-with-sd/build/umlp/backend_symbols"),
        Path.of("examples/testing-with-sd/build/umlp/test_symbols")));
    globalScope.setSymbolPath(symbolPath);
    tool.createSymbolTable(model);

    //make sure model is valid
    tool.checkAllCoCos(model);
  }
}
