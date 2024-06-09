/* (c) https://github.com/MontiCore/monticore */
package gui;

import functionstructure4fenix.DoubleValue;
import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import functionstructure4fenix.Value;
import umlp.backendrte.common.Gem;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class FunctionDetail extends FunctionDetailTOP {
  public FunctionDetail(Function f) {
    super();
    setF(f);
  }

  @Override
  public Consumer<Void> goToFunctionDetailsSub(Function sf) {
    return goToFunctionDetails(sf);
  }

  @Override
  public Consumer<Void> goToFunctionDetailsSuper(Function f) {
    Gem<Function> ownerGem = getF().getOwnerGem();
    if (ownerGem.isPresent()) {
      return goToFunctionDetails(ownerGem.get());
    } else {
      return goToFunctionDetails(getF());
    }
  }

//  @Override
//  public String getSuperFunctionLabel() {
//    Gem<Function> ownerGem = getF().getOwnerGem();
//    if (ownerGem.isPresent()) {
//      return "Back to owning function: " + ownerGem.get().getName();
//    } else {
//      return "No owning function present";
//    }
//  }

  public Consumer<Void> goToFunctionDetails(Function sf) {
    return $ -> {
      Router.navigate("gui","FunctionDetail", "" + sf.getGemId());
    };
  }

  @Override
  public String getValueTextInValue(Port p) {
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

  @Override
  public String getValueTextOutValue(Port p) {
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
