/* (c) https://github.com/MontiCore/monticore */
package gui;

import functionstructure4fenix.DoubleValue;
import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import functionstructure4fenix.Value;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.Random;
import java.util.function.Consumer;

@Component
public class FunctionDetail extends FunctionDetailTOP {

  public FunctionDetail(Function f) {
    super(f);
  }

  @Override
  public Consumer<Object> changeOwnerName (Object f)
  {
    return event -> {
      this.getF().getOwner().setName("TES31231");
    };
  }

  //  @Override
  public Consumer<Object> goToFunctionDetailsSub(Object sf) {
    return goToFunctionDetails(sf);
  }

  @Override
  public Consumer<Object> goToFunctionDetailsSuper(Object f) {
    if (f instanceof Function && ((Function) f).getOwnerGem().isPresent()) {
      return goToFunctionDetails(((Function) f).getOwner());
    } else {
      return goToFunctionDetails(f);
    }
  }



  //  @Override
  public Consumer<Object> goToFunctionDetails(Object sf) {
    return event -> {
      if (sf instanceof Function) {
        Router.navigate("gui","FunctionDetail", "" + ((Function) sf).getGemId());
      }
    };
  }

  @Override
  public String getValueTextInValue(Port p) {
    return getPortStringValue(p);
  }

  @Override
  public String getValueTextOutValue(Port p) {
    return getPortStringValue(p);
  }
  //
  private String getPortStringValue(Port p) {
    if (p.getChannel().getFunctionStream().getValue() == null) {
      return null;
    }
    String res = "";
    for (Value value : p.getChannel().getFunctionStream().getValue()) {
      if (value instanceof DoubleValue) {
        if (value instanceof DoubleValue) {
          res += ((DoubleValue) value).getContent() + ", ";
        }
      }
    }
    return res.substring(0, res.length() - 2);
  }
}
