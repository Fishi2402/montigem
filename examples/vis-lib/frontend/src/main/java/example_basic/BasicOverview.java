/* (c) https://github.com/MontiCore/monticore */
package example_basic;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class BasicOverview extends BasicOverviewTOP {

  private Consumer<Void> navigateTo(String target) {
      return v -> {
          Router.navigate("example_basic", target);
      };
  }

  public Consumer<Void> goToButton() { return navigateTo("GemButtonExample"); }
  public Consumer<Void> goToText() { return navigateTo("GemTextExample"); }
  public Consumer<Void> goToIcon() { return navigateTo("GemIconExample"); }
  public Consumer<Void> goToImage() { return navigateTo("GemImageExample"); }
  public Consumer<Void> goToLink() { return navigateTo("GemLinkExample"); }
  public Consumer<Void> goToLoadingSpinner() { return navigateTo("GemLoadingSpinnerExample"); }
  public Consumer<Void> goToAudio() { return navigateTo("GemAudioExample"); }
  public Consumer<Void> goToVideo() { return navigateTo("GemVideoExample"); }
}
