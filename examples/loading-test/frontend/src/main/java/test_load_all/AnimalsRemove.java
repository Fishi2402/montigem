package test_load_all;

import loadingtests.Animal;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class AnimalsRemove extends AnimalsRemoveTOP {
  @Override
  public Consumer<Void> removeFirst() {
    return Void -> {
        getPerson().removePet(getPerson().getFavouritePet());
    };
  }
}
