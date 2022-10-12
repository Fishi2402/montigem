package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Person;
import hardy.PersonDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
class Person_deleteDeSer extends CommandDeSer<Person_delete> {
    public Optional<Person_delete> fromJson(JsonObject jsonObject) {
      Optional<Person> person = new PersonDeSer().deserialize(jsonObject.toString());
      if(person.isEmpty()){
        return Optional.empty();
      }
      return Optional.of(new Person_delete(person.get()));
    }

    public void buildJson(Person_delete command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("person", new PersonDeSer().serialize(command.getPerson()));
    }
}