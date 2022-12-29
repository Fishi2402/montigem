/* (c) https://github.com/MontiCore/monticore */
package hardy.commands;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import hardy.Car;
import hardy.CarDeSer;
import java.util.Optional;
import umlp.command.CommandDeSer;
public class Car_deleteDeSer extends CommandDeSer<Car_delete> {
    protected Optional<Car_delete> fromJsonObject(JsonObject jsonObject) {
      Optional<Car> car = new CarDeSer().deserialize(jsonObject.getObjectMember("car"));
      if(car.isEmpty()){
        return Optional.empty();
      }

      return Optional.of(new Car_delete(car.get()));
    }

    public void buildJson(Car_delete command, JsonPrinter jsonPrinter) {
        super.buildJson(command, jsonPrinter);
        jsonPrinter.memberJson("car", new CarDeSer().serialize(command.getCar()));
    }
}
