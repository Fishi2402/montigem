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

  public FunctionOverview(GemList functions) {
    super(functions);
  }

  @Override
  public Consumer<Object> goToFunctionDetails(Object f) {
    return event -> {
      if (f instanceof Function) {
        Router.navigate("gui","FunctionDetail", "" + ((Function) f).getGemId());
      }
    };
  }
}
