/* (c) https://github.com/MontiCore/monticore */
package common;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class OtherOverview extends OtherOverviewTOP {

  public Consumer<Void> goToMonaco() { 
      return v -> Router.navigate("example_monaco", "ExampleFromLib", "3"); 
  }
  
  public Consumer<Void> goToFischertechnik() { 
      return v -> Router.navigate("fischertechnik", "FischertechnikExample"); 
  }
  
  public Consumer<Void> goToCAD() { 
      return v -> Router.navigate("example_cad", "GemCADExample");
  }
  
  public Consumer<Void> goToPerson() { 
      return v -> Router.navigate("person", "SimpleOverview"); 
  }
}
