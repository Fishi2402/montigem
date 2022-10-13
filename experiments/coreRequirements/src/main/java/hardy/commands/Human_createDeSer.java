package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Human;
import hardy.HumanDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Human_createDeSer extends CommandDeSer<Human_create> {
    protected Optional<Human_create> fromJsonObject(JsonObject jsonObject) {
      Optional<Human> human = new HumanDeSer().deserialize(jsonObject.getObjectMember("human"));
      if(human.isEmpty()){
        return Optional.empty();
      }
      return Optional.of(new Human_create(human.get()));
    }

    public void buildJson(Human_create command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("human", new HumanDeSer().serialize(command.getHuman()));
    }
}