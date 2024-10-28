/* (c) https://github.com/MontiCore/monticore */
package gui;

import de.se_rwth.commons.logging.Log;
import functionstructure4fenix.Function;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class FunctionOverview extends FunctionOverviewTOP {

  @Override
  public Consumer<Void> goToFunctionDetails(Function f) {
    return $ -> {
      Router.navigate("gui", "FunctionDetail", "" + f.getGemId());
    };
  }
}
