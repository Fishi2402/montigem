package gradle.stat;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.UserJsonString;

import java.util.UUID;

public class TaskStatData {
  final private UUID randomID = UUID.randomUUID();
  private long numberOfInputFiles = 0;
  private long numberOfRuns = 0;
  private String type;

  public TaskStatData(String type) {
    this.type=type;
  }

  public void setNumberOfInputFiles(long count){
    numberOfInputFiles=count;
  }

  public void countRun(){
    numberOfRuns++;
  }
  public JsonElement getData() {
    JsonObject result = new JsonObject();
    result.putMember("type", new UserJsonString(type));
    result.putMember("randomID", new UserJsonString(randomID.toString()));
    result.putMember("numberOfInputFiles", new JsonNumber(numberOfInputFiles+""));
    result.putMember("numberOfRuns", new JsonNumber(numberOfRuns+""));
    return result;
  }
}
