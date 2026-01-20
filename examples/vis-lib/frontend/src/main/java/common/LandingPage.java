/* (c) https://github.com/MontiCore/monticore */
package common;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class LandingPage extends LandingPageTOP {

  @Override
  public void init() {
      super.init();
      setDemoCode(getDemoCodeContent());
  }

  private String getDemoCodeContent() {
      return "component MyPage {\n" +
             "  @GemColumn(\n" +
             "    components = [\n" +
             "      @GemText(value=\"Hello World\"),\n" +
             "      @GemTextInput(labelText=\"Name\"),\n" +
             "      @GemButton(label=\"Send\")\n" +
             "    ]\n" +
             "  );\n" +
             "}";
  }

  public Consumer<Void> goToBasicFromHero() { 
      return v -> Router.navigate("example_basic", "BasicOverview"); 
  }
  
  public Consumer<Void> goToChartsOverviewFromHero() { 
      return v -> Router.navigate("example_charts", "ChartsOverview"); 
  }
  
  public Consumer<Void> goToBasicFromFeatures() { 
      return v -> Router.navigate("example_basic", "BasicOverview"); 
  }
  
  public Consumer<Void> goToInput() { 
      return v -> Router.navigate("example_input", "InputOverview"); 
  }
  
  public Consumer<Void> goToArrange() { 
      return v -> Router.navigate("example_arrange", "ArrangeOverview"); 
  }
  
  public Consumer<Void> goToChartsOverviewFromFeatures() { 
      return v -> Router.navigate("example_charts", "ChartsOverview");
  }
  
  public Consumer<Void> goToOther() { 
      return v -> Router.navigate("common", "OtherOverview");
  }
}
