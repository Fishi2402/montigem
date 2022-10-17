package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Human;
import hardy.HumanDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Human_deleteDeSer extends CommandDeSer<Human_delete> {
    protected Optional<Human_delete> fromJsonObject(JsonObject jsonObject) {
      Optional<Human> human = new HumanDeSer().deserialize(jsonObject.getObjectMember("human"));
      if(human.isEmpty()){
        return Optional.empty();
      }
      return Optional.of(new Human_delete(human.get()));
    }

    public void buildJson(Human_delete command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("human", new HumanDeSer().serialize(command.getHuman()));
    }
}