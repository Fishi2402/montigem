package test_gui;

import def.dom.MouseEvent;
import umlp.jsweet.extension.annotation.Component;

@Component
public class HostListenerTest extends HostListenerTestTOP {

  @HostListener(eventName = "document:mousemove", args = {"$event"})
  public void onMouseMove(MouseEvent event) {
    this.setMessage("mouse was moved to " + event.x + "/" + event.clientX);
  }
}
