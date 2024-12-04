/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.coco;

import de.monticore.expressions.cocos.ExpressionValid;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.guidsl._cocos.GUIComponentDeclarationIsPresent;
import de.monticore.guidsl._cocos.GUIDSLCoCoChecker;
import de.monticore.guidsl._cocos.GUIDSLCoCos;
import de.monticore.guidsl._cocos.GUIMultipleNameArgs;
import de.monticore.guidsl._cocos.GUINameArgExists;
import de.monticore.guidsl._cocos.GUIParamDefaultFitsType;
import de.monticore.guidsl._cocos.GUIRangeIterateControlStartAndEndAreInt;
import de.monticore.guidsl._cocos.GUIShortIterateCheckIterable;
import de.monticore.guidsl.types.check.util.GUIDSLTypeTraverserFactory;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.types3wrapper.TypeCheck3AsTypeCalculator;

public class GUIDSLCoCosForUMLP extends GUIDSLCoCos {

  public GUIDSLCoCoChecker createChecker() {
    TypeCalculator tc = new TypeCheck3AsTypeCalculator(
        GUIDSLTypeTraverserFactory.getInstance().createTraverserForOO(type4Ast),
        type4Ast,
        new CommonExpressionsLValueRelations()
    );
    GUIDSLCoCoChecker checker = new GUIDSLCoCoChecker();
    checker.addCoCo(new ExpressionValid(tc));
    checker.addCoCo(new GUIParamDefaultFitsType());
    checker.addCoCo(new EnhancedGuardConditionIsBoolean());
    checker.addCoCo(new GUIRangeIterateControlStartAndEndAreInt());
    checker.addCoCo(new GUINameArgExists());
    checker.addCoCo(new GUIMultipleNameArgs());
    checker.addCoCo(new GUIComponentDeclarationIsPresent());
    checker.addCoCo(new GUIShortIterateCheckIterable());

    checker.addCoCo(new GuiNoSingletonList());
    return checker;
  }
}
