package test_update_after_build;

import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class PersonBuilder extends PersonBuilderTOP {
  @Override
  public Consumer<Void> buildPerson() {
    return (u) -> {
      // Default Age
      this.getPerson().setAge(42);
      this.getPerson().build();
    };
  }
}
