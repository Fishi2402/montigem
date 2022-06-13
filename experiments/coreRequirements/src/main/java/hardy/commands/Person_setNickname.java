package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import hardy.Person;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.command.error.MontiGemError;
import umlp.command.error.MontiGemErrorCode;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Person_setNickname extends Command<Void> {
  protected long gemId;
  protected String nickname;

  public Person_setNickname(long gemId, String nickname) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Person_setNickname"
    );

    this.gemId = gemId;
    this.nickname = nickname;
  }

  @Override
  public StatusResult doRun() {
    Optional<Person> personOpt = FlensburgManager.personOpt(gemId);
    if(personOpt.isPresent()){
      personOpt.get().setNickname(nickname);
      return StatusResult.ok(getId());
    }else{
      return StatusResult.error(getId(), new MontiGemError(MontiGemErrorCode.UNKNOWN, null, "Did not find Person with id " + gemId));
    }
  }

  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.singleton(gemId);
  }
}
