/* (c) https://github.com/MontiCore/monticore */
package functionstructure;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import umlp.backendrte.common.NotificationScope;

import java.util.Optional;

public class Type extends TypeTOP {
  SymTypeExpression symTypeExpression;

  @Override
  public void setName(String _name, NotificationScope scope) {
    this.symTypeExpression = SymTypeExpressionFactory.createPrimitive(_name);
    // TODO Parse SymTypeExpression

    super.setName(_name, scope);
  }
}
