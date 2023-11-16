/* (c) https://github.com/MontiCore/monticore */

package scexample;

import AmpelSC.AmpelSC;


// TODO: AmpelSC actions can not effect this
// Pass reference
// or merge classes?
public class Ampel extends AmpelTOP{
  protected AmpelSC ampelSC = new AmpelSC();

  public void timeout(){
    ampelSC.timeout();
  }
}
