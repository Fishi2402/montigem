/* (c) https://github.com/MontiCore/monticore */
package guidslpaper.commands;

import de.se_rwth.commons.logging.Log;
import guidslpaper.GuidslpaperManager;
import guidslpaper.Person;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Person_create extends Person_createTOP {

  public Person_create(Person person) {
    super(person);
  }

  public StatusResult doAction () {
    GuidslpaperManager.addWithScopePerson(person, NotificationScope.LOCAL);
    Log.info("Created new person. Start extraction of PipelineAnalysisData","Person_create");

    return StatusResult.ok(getId());
  }
}
