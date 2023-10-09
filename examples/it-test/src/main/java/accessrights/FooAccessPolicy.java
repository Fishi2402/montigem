package accessrights;

import accessrights.commands.Foo_setA;
import umlp.backendrte.command.Command;

public class FooAccessPolicy extends FooAccessPolicyTOP {

  @Override
  public boolean checkUpdatePermission(Command command) {
    return !(command instanceof Foo_setA);
  }
}
