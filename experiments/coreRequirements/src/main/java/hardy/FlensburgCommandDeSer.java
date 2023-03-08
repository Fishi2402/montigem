/* (c) https://github.com/MontiCore/monticore */
package hardy;

import hardy.commands.Address_get_filteredDeSer;
import hardy.commands.Car_get_filteredDeSer;
import hardy.commands.Human_get_filteredDeSer;
import hardy.commands.Person_get_filteredDeSer;
import hardy.requests.GetFreeGemIdRequestDeSer;
import umlp.backendrte.common.AggregatedCommandDeSer;

public class FlensburgCommandDeSer extends AggregatedCommandDeSer {

  public FlensburgCommandDeSer(){
    // Standard
    deSerMap.put("hardy.requests.GetFreeGemIdRequest", new GetFreeGemIdRequestDeSer());

    // Generated
    deSerMap.put("hardy.commands.Person_get", new hardy.commands.Person_getDeSer());
    deSerMap.put("hardy.commands.Person_setNickname", new hardy.commands.Person_setNicknameDeSer());
    deSerMap.put("hardy.commands.Person_removeOwns", new hardy.commands.Person_removeOwnsDeSer());
    deSerMap.put("hardy.commands.Car_create", new hardy.commands.Car_createDeSer());
    deSerMap.put("hardy.commands.Car_get", new hardy.commands.Car_getDeSer());
    deSerMap.put("hardy.commands.Address_get", new hardy.commands.Address_getDeSer());
    deSerMap.put("hardy.commands.Human_get", new hardy.commands.Human_getDeSer());
    deSerMap.put("hardy.commands.Human_error_age", new hardy.commands.Human_error_ageDeSer());
    deSerMap.put("hardy.commands.Person_setAge", new hardy.commands.Person_setAgeDeSer());
    deSerMap.put("hardy.commands.Human_set_age", new hardy.commands.Human_set_ageDeSer());
    deSerMap.put("hardy.commands.Human_delete", new hardy.commands.Human_deleteDeSer());
    deSerMap.put("hardy.commands.Person_setMainResidence", new hardy.commands.Person_setMainResidenceDeSer());
    deSerMap.put("hardy.commands.Person_create", new hardy.commands.Person_createDeSer());
    deSerMap.put("hardy.commands.Person_delete", new hardy.commands.Person_deleteDeSer());
    deSerMap.put("hardy.commands.Car_delete", new hardy.commands.Car_deleteDeSer());
    deSerMap.put("hardy.commands.Address_delete", new hardy.commands.Address_deleteDeSer());
    deSerMap.put("hardy.commands.Human_create", new hardy.commands.Human_createDeSer());
    deSerMap.put("hardy.commands.Person_addOwns", new hardy.commands.Person_addOwnsDeSer());
    deSerMap.put("hardy.commands.Address_create", new hardy.commands.Address_createDeSer());
    deSerMap.put("hardy.commands.associations.Visits_add", new hardy.commands.associations.Visits_addDeSer());
    deSerMap.put("hardy.commands.associations.Visits_remove", new hardy.commands.associations.Visits_removeDeSer());
    deSerMap.put("hardy.commands.associations.Visits_set", new hardy.commands.associations.Visits_setDeSer());
    deSerMap.put("hardy.commands.Address_get_filtered", new Address_get_filteredDeSer());
    deSerMap.put("hardy.commands.Car_get_filtered", new Car_get_filteredDeSer());
    deSerMap.put("hardy.commands.Human_get_filtered", new Human_get_filteredDeSer());
    deSerMap.put("hardy.commands.Person_get_filtered", new Person_get_filteredDeSer());
  }
}
