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
}
