package hardy.commands;

// GEN-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.GemResult;
import umlp.rte.Gem;

import java.util.Collection;
import java.util.Collections;

// Gibt die Anzahl aller Humans zurück
public class Human_size extends Command<Integer> {

  public Human_size() {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.Human_size"
    );
  }

  @Override
  public GemResult<Integer> doRun() {
    // TODO: Optimieren, Liste muss nicht ganz geladen werden
    return new GemResult<>(getId(), new Gem<>(FlensburgManager.getHumanList().size()), "java.lang.Integer");
  }

  // undo macht keinen Sinn

  // TODO: updates to the returned LazyList need to be send?
  @Override
  public Collection<Long> getEffectedGemIds() {
    return Collections.emptyList();
  }
}
