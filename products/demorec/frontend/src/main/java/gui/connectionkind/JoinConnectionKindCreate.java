/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.JoinConnectionKind;
import demorec.service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class JoinConnectionKindCreate extends JoinConnectionKindCreateTOP {

  protected BatteryPack currentPack = null;

  public JoinConnectionKindCreate(BatteryPackService service) {
    super();
    service.addObserverFunction((BatteryPack p) -> {
      this.currentPack = p;
    });
  }

  public Boolean isDisabled(JoinConnectionKind kind) {
    return kind.isBuildFinished();
  }

  public Consumer<Void> createNewConnectionKind()  {
    return $ -> {
      if(!this.getConnectionKind().isBuildFinished()) {
        Optional<JoinConnectionKind> kind = (Optional<JoinConnectionKind>) this.getConnectionKind().build();
        if(this.currentPack != null) {
          this.getConnectionKind().setBatteryPack(this.currentPack);
        } else {
          Log.warn("No Pack available for Connection");
        }
        if(kind.isPresent()) {
          this.getCreateJoinConnectionKind().accept(kind.get());
        }
      } else {
        //console.debug("New Connection is not buildable")
        this.getConnectionKind().build();
      }
    };
  }

}
