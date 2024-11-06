package loadingtests.commands;

import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.LoadingTestsManager;
import umlp.backendrte.command.StatusResult;

import java.util.List;

public class ReplaceFoodItem extends ReplaceFoodItemTOP {
  public ReplaceFoodItem(long aGemId) {
    super(aGemId);
  }

  @Override
  public StatusResult doAction() {
    Animal a = LoadingTestsManager.getAnimalSure(getAGemId());
    List<Food> foods = a
        .getFoodList();

    String firstName = foods.get(0).getName();

    // First add some (new) food - this food will not be already loaded on the client
    a.addFood(LoadingTestsManager.foodBuilder().name(firstName + "#").build().get());

    // Then remove the first food
    a.removeFood(0);

    return StatusResult.ok(getId());
  }
}
