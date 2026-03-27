/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.JointConnectionKind;
import demorec.service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class JointConnectionKindCreate extends JointConnectionKindCreateTOP {

  protected BatteryPack currentPack = null;

  public JointConnectionKindCreate(BatteryPackService service) {
    super();
    service.addObserverFunction((BatteryPack p) -> {
      this.currentPack = p;
    });
  }

  public Boolean isDisabled(JointConnectionKind kind) {
    return kind.isBuildFinished();
  }

  public Consumer<Void> createNewConnectionKind()  {
    return $ -> {
      if(!this.getConnectionKind().isBuildFinished()) {
        Optional<JointConnectionKind> kind = (Optional<JointConnectionKind>) this.getConnectionKind().build();
        if(this.currentPack != null) {
          this.getConnectionKind().setBatteryPack(this.currentPack);
        } else {
          Log.warn("No Pack available for Connection");
        }
        if(kind.isPresent()) {
          this.getCreateJointConnectionKind().accept(kind.get());
        }
      } else {
        this.getConnectionKind().build();
      }
    };
  }

}
