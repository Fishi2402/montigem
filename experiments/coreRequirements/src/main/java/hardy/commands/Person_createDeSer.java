package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Person;
import hardy.PersonDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Person_createDeSer extends CommandDeSer<Person_create> {
    protected Optional<Person_create> fromJsonObject(JsonObject jsonObject) {
      Optional<Person> person = new PersonDeSer().deserialize(jsonObject.getObjectMember("person"));
      if(person.isEmpty()){
        return Optional.empty();
      }
      return Optional.of(new Person_create(person.get()));
    }

    public void buildJson(Person_create command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("person", new PersonDeSer().serialize(command.getPerson()));
    }
}