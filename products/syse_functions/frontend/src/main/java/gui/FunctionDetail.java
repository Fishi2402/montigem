/* (c) https://github.com/MontiCore/monticore */
package gui;

import functionstructure4fenix.DoubleValue;
import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import functionstructure4fenix.Value;
import umlp.backendrte.common.Gem;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.Random;
import java.util.function.Consumer;

@Component
public class FunctionDetail extends FunctionDetailTOP {

  public FunctionDetail(Function f) {
    super();
    setF(f);
  }

  @Override
  public Consumer<Void> changeOwnerName(Function f) {
    return $ -> {
      this.getF().getOwner().setName("TES31231");
    };
  }

  //  @Override
  public Consumer<Void> goToFunctionDetailsSub(Function sf) {
    return goToFunctionDetails(sf);
  }

  @Override
  public Consumer<Void> goToFunctionDetailsSuper(Function f) {
    if (getF().getOwnerGem().isPresent()) {
      return goToFunctionDetails(getF().getOwnerGem().get());
    } else {
      return goToFunctionDetails(f);
    }
  }


  public Consumer<Void> goToFunctionDetails(Function sf) {
    return $ -> {
      Router.navigate("gui","FunctionDetail", "" + sf.getGemId());
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
    if (p.getChannel().getFunctionStream().getValueList() == null) {
      return null;
    }
    String res = "";
    for (Value value : p.getChannel().getFunctionStream().getValueList()) {
      if (value instanceof DoubleValue) {
        if (value instanceof DoubleValue) {
          res += ((DoubleValue) value).getContent() + ", ";
        }
      }
    }
    return res.substring(0, res.length() - 2);
  }
}
