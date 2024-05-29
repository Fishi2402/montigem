/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.input.GemTextInputTypes;

import java.util.Optional;

public class Option {
  private Optional<String> value;
  private Optional<String> display;

  public Option(String value, String display) {
    this.value = Optional.of(value);
    this.display = Optional.of(display);
  }

  public Option(String value) {
    this.value = Optional.of(value);
    this.display = Optional.of(value);
  }

  public Optional<String> getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = Optional.of(value);
  }

  public Optional<String> getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = Optional.of(display);
  }
}
