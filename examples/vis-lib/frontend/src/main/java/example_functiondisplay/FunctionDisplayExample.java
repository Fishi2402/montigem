/* (c) https://github.com/MontiCore/monticore */
package example_functiondisplay;

import mc.fenix.functiondisplay.functionstructure4fenix.Channel4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Channel4DisplayBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.Function4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Function4DisplayBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.FunctionActionBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.Port4Display;
import mc.fenix.functiondisplay.functionstructure4fenix.Port4DisplayBuilder;
import mc.fenix.functiondisplay.functionstructure4fenix.FunctionAction;
import umlp.jsweet.extension.annotation.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

@Component
public class FunctionDisplayExample extends FunctionDisplayExampleTOP {

  @Override
  protected void initClickedInfo() {
    this.setClickedInfo("Nothing");
    super.initClickedInfo();
  }

  @Override
  protected void initContextMenuActions() {
    this.setContextMenuActions(Arrays.asList(
        new FunctionActionBuilder()
            .label("Navigation")
            .functionClick(onNavClick()).build().get(),
        new FunctionActionBuilder()
            .label("Details")
            .functionClick(onDetClick()).build().get()
    ));
    super.initContextMenuActions();
  }

  public Consumer<Channel4Display> onChannelClick() {
    return c -> {
        this.setClickedInfo("Channel clicked");
        ArrayList<Channel4Display> current = new ArrayList<>(this.getSelectedChannels());
        if (current.contains(c)) {
            current.remove(c);
        } else {
            current.add(c);
        }
        this.setSelectedChannels(current);
    };
  }

  public Consumer<Function4Display> onFunctionClick() {
    return f -> {
        this.setClickedInfo("Component clicked: " + (f.getName() != null ? f.getName() : ""));
        ArrayList<Function4Display> current = new ArrayList<>(this.getSelectedFunctions());
        if (current.contains(f)) {
            current.remove(f);
        } else {
            current.add(f);
        }
        this.setSelectedFunctions(current);
    };
  }

  public Consumer<Port4Display> onPortClick() {
    return p -> {
        this.setClickedInfo("Port clicked: " + (p.getName() != null ? p.getName() : ""));
        ArrayList<Port4Display> current = new ArrayList<>(this.getSelectedPorts());
        if (current.contains(p)) {
            current.remove(p);
        } else {
            current.add(p);
        }
        this.setSelectedPorts(current);
    };
  }

  public Consumer<Function4Display> onNavClick() {
    return target -> {
        String name = target != null ? target.getName() : "unknown";
        this.setClickedInfo("Navigation to: " + name);
    };
  }

  public Consumer<Function4Display> onDetClick() {
    return target -> {
        String name = target != null ? target.getName() : "unknown";
        this.setClickedInfo("Details for: " + name);
    };
  }

  public Consumer<Void> onOutsideClick() {
    return Void -> {
        this.setClickedInfo("Background clicked");
        this.setSelectedChannels(new ArrayList<>());
        this.setSelectedFunctions(new ArrayList<>());
        this.setSelectedPorts(new ArrayList<>());
    };
  }

  public Function4Display getColoredData() {
      String[] colors = {
          "#00549F", "#57ab27", "#cc071e", "#f6a800", "#0098A1", "#612158"
      };

      ArrayList<Function4Display> subs = new ArrayList<>();
      
      for (int i = 0; i < colors.length; i++) {
          String color = colors[i];

          Function4Display f = new Function4Display();
          f.setName("Function " + i);
          f.setBackgroundColor(color);
          f.setBorderColor("black");
          f.setBorderColorHover("#444444");

          if (i == 0) {
              // Add a car icon to the first component
              f.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/1/10/Directions_car_black_24dp.svg");
          }

          Port4Display pOut = new Port4Display();
          pOut.setName("out");
          pOut.setBackgroundColor("white");
          pOut.setBorderColor(color);
          pOut.setBorderColorHover(color);
          f.setOutList(Arrays.asList(pOut));
          
          Port4Display pIn = new Port4Display();
          pIn.setName("in");
          pIn.setBackgroundColor("white");
          pIn.setBorderColor(color);
          pIn.setBorderColorHover(color);
          f.setInList(Arrays.asList(pIn));
          
          subs.add(f);
      }

      // Add colored channels between the functions
      for (int i = 0; i < subs.size() - 1; i++) {
          Function4Display src = subs.get(i);
          Function4Display target = subs.get(i + 1);
          
          Port4Display out = src.getOutList().get(0);
          Port4Display in = target.getInList().get(0);
          
          Channel4Display channel = new Channel4Display();
          // Use the source function's color for the channel
          String color = colors[i];
          channel.setLineColor(color);
          channel.setLineColorHover(color);
          
          out.setChannel4Display(channel);
          in.setChannel4Display(channel);
          channel.setPort4DisplayList(Arrays.asList(out, in));
      }
      
      Function4Display root = new Function4Display();
      root.setName("colored_root");
      root.setSubFunctionList(subs);
      return root;
  }

  @Override
  public Function4Display getData() {
    // Component a
    Port4Display aOut = new Port4Display();
    aOut.setName("out");
    Function4Display a = new Function4Display();
    a.setName("a");
    a.setOutList(Arrays.asList(aOut));

    // Component b
    Port4Display bIn1 = new Port4Display();
    bIn1.setName("in1");
    Function4Display b = new Function4Display();
    b.setName("b");
    b.setInList(Arrays.asList(bIn1));

    // Component c
    Port4Display cIn2 = new Port4Display();
    cIn2.setName("in2");
    Function4Display c = new Function4Display();
    c.setName("c");
    c.setInList(Arrays.asList(cIn2));

    // Component d
    Port4Display dIn2 = new Port4Display();
    dIn2.setName("in2");
    Function4Display d = new Function4Display();
    d.setName("d");
    d.setInList(Arrays.asList(dIn2));

    // Channel from a.out to b.in1, c.in2, d.in2
    Channel4Display channel = new Channel4Display();
    
    aOut.setChannel4Display(channel);
    bIn1.setChannel4Display(channel);
    cIn2.setChannel4Display(channel);
    dIn2.setChannel4Display(channel);
    
    ArrayList<Port4Display> portList = new ArrayList<>();
    portList.add(aOut);
    portList.add(bIn1);
    portList.add(cIn2);
    portList.add(dIn2);
    channel.setPort4DisplayList(portList);

    Function4Display root = new Function4Display();
    root.setName("root");
    root.setSubFunctionList(Arrays.asList(a, b, c, d));
    return root;
  }

  @Override
  public Function4Display getWideData() {
    ArrayList<Function4Display> subFunctions = new ArrayList<>();
    
    // Create a chain of many components to make the diagram wide
    int numComponents = 15;
    Port4Display previousOut = null;
    
    for (int i = 0; i < numComponents; i++) {
        Function4Display f = new Function4Display();
        f.setName("comp_" + i);
        
        // Input port (for all except the first)
        if (previousOut != null) {
            Port4Display pIn = new Port4Display();
            pIn.setName("in");
            f.setInList(Arrays.asList(pIn));
            
            // Connect previous output to this input
            Channel4Display channel = new Channel4Display();
            previousOut.setChannel4Display(channel);
            pIn.setChannel4Display(channel);
            
            ArrayList<Port4Display> portList = new ArrayList<>();
            portList.add(previousOut);
            portList.add(pIn);
            channel.setPort4DisplayList(portList);
        }
        
        // Output port (for all except the last)
        if (i < numComponents - 1) {
            Port4Display pOut = new Port4Display();
            pOut.setName("out");
            f.setOutList(Arrays.asList(pOut));
            previousOut = pOut;
        }
        
        subFunctions.add(f);
    }
    
    Function4Display root = new Function4Display();
    root.setName("wide_root");
    root.setSubFunctionList(subFunctions);
    
    return root;
  }

  public Function4Display getImageData() {
    Function4Display car1 = new Function4Display();
    car1.setName("Car 1");
    car1.setImageUrl("assets/images/Car.png");
    car1.setBackgroundColor("#e0e0e0");
    car1.setBorderColor("black");

    Function4Display car2 = new Function4Display();
    car2.setName("Car 2");
    car2.setImageUrl("assets/images/Car.png");
    car2.setBackgroundColor("#f0f0f0");
    car2.setBorderColor("black");

    // Add ports and connection
    Port4Display p1 = new Port4Display();
    p1.setName("out");
    car1.setOutList(Arrays.asList(p1));

    Port4Display p2 = new Port4Display();
    p2.setName("in");
    car2.setInList(Arrays.asList(p2));

    Channel4Display channel = new Channel4Display();
    channel.setLineColor("blue");
    p1.setChannel4Display(channel);
    p2.setChannel4Display(channel);
    channel.setPort4DisplayList(Arrays.asList(p1, p2));

    Function4Display root = new Function4Display();
    root.setName("image_root");
    root.setSubFunctionList(Arrays.asList(car1, car2));
    return root;
  }
}
