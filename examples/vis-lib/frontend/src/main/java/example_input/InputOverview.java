/* (c) https://github.com/MontiCore/monticore */
package example_input;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class InputOverview extends InputOverviewTOP {

  private Consumer<Void> navigateTo(String target) {
      return v -> {
          Router.navigate("example_input", target);
      };
  }

  public Consumer<Void> goToTextInput() { return navigateTo("GemTextInputExample"); }
  public Consumer<Void> goToTextareaInput() { return navigateTo("GemTextareaInputExample"); }
  public Consumer<Void> goToCheckBoxInput() { return navigateTo("GemCheckBoxInputExample"); }
  public Consumer<Void> goToRadioInput() { return navigateTo("GemRadioInputExample"); }
  public Consumer<Void> goToDropdownInput() { return navigateTo("GemDropdownInputExample"); }
  public Consumer<Void> goToAutocompleteInput() { return navigateTo("GemAutocompleteInputExample"); }
  public Consumer<Void> goToRangeInput() { return navigateTo("GemRangeInputExample"); }
  public Consumer<Void> goToChat() { return navigateTo("GemChatExample"); }
}
