package test_update_of_builder;

import loadingtests.Food;
import loadingtests.LoadingTestsManager;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component()
public class AnimalAddFood extends AnimalAddFoodTOP {
  private boolean requestedBackend  = false;
  private List<Option> foodOptions = new ArrayList<>();

  @Override
  public List<Option> getOptions() {
    if (!this.requestedBackend) {
      System.out.println("Requesting Options");
      for (Food f : LoadingTestsManager.getFoodList()) {
        this.foodOptions.add(new Option(f.getGemId() + "", f.getName()));
      }
      System.out.println("Updating food options" + this.foodOptions.size());
      this.requestedBackend=true;
    }

    return this.foodOptions;
  }

  @Override
  public void setFoodEntry(String value) {
    super.setFoodEntry(value);

    Food f = LoadingTestsManager.getFoodSure(Long.parseLong(value));
    this.getAnimal().addFood(f);
    //TODO: this.getAnimal()._internal_getFoodGemList().loadAll();

    // Set all other values to default stuff
    this.getAnimal().setName("New Animal");
    this.getAnimal().setLegs(0);
  }
}
