/* (c) https://github.com/MontiCore/monticore */
package stanley.personcreate;

/* HWC-P extends generated java-gui PersonEditTOP
   generated from gui-model PersonEdit.gui
*/

public class PersonCreate extends PersonCreateTOP {

  public PersonCreate() {
    super();
  }

  @Override
  public void create() {
    try {
      if (!this.pb.isBuildFinished() && this.pb.isBuildable()) {
        this.pb.build();
      }
    } catch (IllegalStateException ex) {
      // TODO something goes completely wrong in client (should never happen) or server responds
      //  with an error, handle, but do not stop execution, GUI is never unresponsive
    }
    // some implementation which translates to standard Angular navigation
    // rte.navigate()
  }

}
