/* (c) https://github.com/MontiCore/monticore */
package gradle.stat;

import de.monticore.symboltable.serialization.json.JsonElement;

public interface GradleTaskStatistic {
  public JsonElement getGradleStatisticData();
}
