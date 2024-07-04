package domain.commands;

import domain.DomainManager;
import domain.Person;
import umlp.backendrte.command.SimpleResult;

import java.time.LocalDate;

public class IsOld extends IsOldTOP {

  public IsOld(long pGemId) {
    super(pGemId);
  }

  @Override
  public SimpleResult<Boolean> doAction() {
    Person p = DomainManager.getPersonSure(pGemId);
    if (p.getBirthday().isAfter(LocalDate.now())) {
      return new SimpleResult<>(pGemId, false, "domain.Person");
    }
    return new SimpleResult<>(pGemId, true, "domain.Person");
  }
}
