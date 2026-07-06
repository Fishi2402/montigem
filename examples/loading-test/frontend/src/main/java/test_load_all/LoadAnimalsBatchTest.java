/* (c) https://github.com/MontiCore/monticore */
package test_load_all;

import jsweet.lang.Replace;
import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component()
public class LoadAnimalsBatchTest extends LoadAnimalsBatchTestTOP {

  @Override
  @Replace("return parseInt(localStorage.getItem('testBatchSize')) || 0;")
  public Integer getBatchSize() {
    return super.getBatchSize();
  }

  @Override
  @Replace("return parseInt(localStorage.getItem('testNumberOfObjects')) || 0;")
  public Integer getNumberOfObjects() {
    return super.getNumberOfObjects();
  }

  @Override
  protected void initAnimals() {
    List<Animal> animals = new ArrayList<>();
    List<Long> createdAnimalIds = getAnimalIds();
    if (this.getBatchSize() > 0) {
      for (int i = 0; i < createdAnimalIds.size(); i += this.getBatchSize()) {
        int endIndex = Math.min(i + this.getBatchSize(), createdAnimalIds.size());
        List<Long> idsBatchArray = createdAnimalIds.subList(i, endIndex);
        List<Long> idsBatchList = new ArrayList<>();
        idsBatchList.addAll(idsBatchArray);
        animals.addAll(LoadingTestsManager.loadAllAnimalWithIDs(idsBatchList));
      }
    } else {
      animals = LoadingTestsManager.getAnimalList();
    }

    List<Long> loadedAnimalIds = new ArrayList<>();
    for (Animal animal : animals) {
      loadedAnimalIds.add(animal.getGemId());
    }
  }

  private List<Long> getAnimalIds() {
    return LoadingTestsManager.getAnimalList().getIDs();
  }
}
