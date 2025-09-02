/* (c) https://github.com/MontiCore/monticore */

import {Component, ElementRef, Input, ViewChild} from "@angular/core";
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { config, FunctionDisplayComponent } from '../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/FunctionDisplayComponent';
import ELK, {ElkNode} from 'elkjs/lib/elk.bundled';
import { Function4Display } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Function4Display";
import { Function4DisplayBuilder } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Function4DisplayBuilder";
import { Port4Display } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Port4Display";
import { Channel4Display } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Channel4Display";
import { Port4Elk, Funktion4Elk } from "./FunctionStructure4Fenix";

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  templateUrl: config.templateUrl,
})
export class FunctionDisplay extends FunctionDisplayComponent {
  public _data: Function4Display;


  elk = new ELK();

  private elkdata :Funktion4Elk;

  private connectionMap = new Map<string, Channel4Display>();

  private channelIdMap = new Map<string, Channel4Display>();

  private portElkMap = new Map<Port4Display, Port4Elk>();
  private functionSvgMap = new Map<Function4Display, Element>();
  private channelSvgMap = new Map<Channel4Display, Set<Element>>();
  private portSvgMap = new Map<Port4Display, Element>();


  private static wrapperId = '----------------'

  @ViewChild('container', { static: false }) container!: ElementRef;

  @Input()
  public set data(data: Function4Display) {
    if (data == null) {return;}
    this.connectionMap = new Map<string, Channel4Display>();
    this.channelIdMap = new Map<string, Channel4Display>();
    this._data = data;
    this.elkdata = this.funk2elkfunk(this.addWrapper(data), 1);
    let transformed = this.transform(this.elkdata);
    let layouted = this.elk.layout(transformed, {
      layoutOptions: {
        'elk.port.borderOffset': "-10",
        'elk.nodeLabels.placement': "[H_CENTER, V_CENTER, INSIDE]",
        //"elk.spacing.labelNode": 10, // https://eclipse.dev/elk/reference/options/org-eclipse-elk-spacing-labelNode.html
        "elk.layered.spacing.baseValue": "40",
        "elk.spacing.portPort": "10", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-spacing-portPort.html
        "elk.spacing.portsSurrounding": "[top=10,left=10,bottom=10,right=10]",
        "elk.nodeLabels.padding": "[top=30,left=10,bottom=10,right=10]",
        "elk.layered.considerModelOrder.portModelOrder": "true",
        "elk.portLabels.placement": "INSIDE", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-portLabels-placement.html
        "elk.portAlignment.west": "BEGIN",
        "elk.portAlignment.east": "END",
        "elk.alignment": "AUTOMATIC", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-alignment.html
        "elk.padding": "[top=30,left=30,bottom=30,right=30]", // padding to child nodes https://eclipse.dev/elk/reference/options/org-eclipse-elk-padding.html
        //"elk.layered.nodePlacement.favorStraightEdges": false, // https://eclipse.dev/elk/reference/options/org-eclipse-elk-layered-nodePlacement-favorStraightEdges.html
        "elk.layered.nodePlacement.strategy": "SIMPLE", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-layered-nodePlacement-strategy.html
      }
    });
    layouted.then(v => {
      const containerElement = this.container.nativeElement;

      // Entfernt alle bestehenden Kinder im container
      if (containerElement.firstChild) {
        containerElement.innerHTML = ''; // Löscht den aktuellen Inhalt
      }

      // Fügt das neue Element hinzu
      containerElement.appendChild(this.renderNode(v));

      // Outside-Click-Listener hinzufügen
      this.addOutsideClickListener();
    });

    layouted.catch(e => console.error(e));

  }


  @Input()
  public set selectedFunctions(f: Function4Display[]){
    this.setSelectedFunctions(f);
    if (f == null){
      return;
    }
    this.functionSvgMap.forEach((value, key) => {
      if (f.includes(key)){
        this.markElement(value, key.getBorderColorHover());
      } else {
        this.unmarkElement(value, key.getBorderColor());
      }
    })
  }

  @Input()
  public set selectedChannels(c: Channel4Display[]){
    this.setSelectedChannels(c);
    if (c == null){
      return;
    }
    this.channelSvgMap.forEach((values, key) => {
      values.forEach(value => {
        if (c.includes(key)){
          this.markElement(value, key.getLineColorHover());
        } else {
          this.unmarkElement(value, key.getLineColor());
        }
      })
    })
  }

  @Input()
  public set selectedPorts(p: Port4Display[]){
    this.setSelectedPorts(p);
    if (p == null){
      return;
    }
    this.portSvgMap.forEach((value, key) => {
      if (p.includes(key)){
        this.markElement(value, key.getBorderColorHover());
      } else {
        this.unmarkElement(value, key.getBorderColor());
      }
    })
  }

  ngOnInit() {
  }

  constructor(private sanitizer: DomSanitizer) {
    super();
  }

  private generateID (): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  //Adds a wrapper to the function to make it a valid root for the elk layouter
  private addWrapper(funk: Function4Display): Function4Display {
    return new Function4DisplayBuilder()
      .name(FunctionDisplay.wrapperId)
      .subFunction([funk])
      .owner(undefined)
      .build();
  }

  //Transforms a Function4Display into a Funktion4Elk recursively until depth is reached
  private funk2elkfunk(funk: Function4Display, depth: number): Funktion4Elk {

    let ret : Funktion4Elk = {name: funk.getName(), parts: null, connections: null, ports:null, self:funk};
    let ports: Port4Elk[] = this.getTransformedPorts(funk, ret);
    if (depth < 0){
      return {name: funk.getName(), parts: [], connections: [], ports:ports, self:funk};
    }
    let subports : Port4Elk[] = [];
    let subfunkElks : Funktion4Elk[] = [];

    //get all the subfunctions and their ports
    //calls itself recursively
    //Has to be done before creating the connections because connections require the portMap to be filled
    if (funk.getSubFunctionList() != null) {
      for (let sf of funk.getSubFunctionList()) {
        let sfelk = this.funk2elkfunk(sf, depth - 1);
        subfunkElks.push(sfelk);
        subports.push(...this.getTransformedPorts(sf, sfelk));
      }
    }

    let connections : [Port4Elk,Port4Elk][] = [];
    for(let port of ports){
      for (let innerPort of port.self.getChannel4Display().getPort4DisplayList()){
        //create connections from port to subfunctions
        if (this.portElkMap.has(innerPort) && this.portElkMap.get(innerPort).isIn && subports.includes(this.portElkMap.get(innerPort))){
          connections.push([port, this.portElkMap.get(innerPort)]);
          this.connectionMap.set(this.portsToString(port, this.portElkMap.get(innerPort)), port.self.getChannel4Display());
        }
        //create connections from subfunctions to port
        if (this.portElkMap.has(innerPort) && !this.portElkMap.get(innerPort).isIn && subports.includes(this.portElkMap.get(innerPort))
          && (this.portElkMap.get(innerPort) != port)){
          connections.push([this.portElkMap.get(innerPort),port])
          this.connectionMap.set(this.portsToString(this.portElkMap.get(innerPort), port), port.self.getChannel4Display());
        }
      }
      //create direct connections between ports without a subfunction
      for(let toPort of ports){
        if (toPort != port && port.isIn && !toPort.isIn && toPort.self.getChannel4Display().getPort4DisplayList().includes(port.self)){
          connections.push([port,toPort]);
          this.connectionMap.set(this.portsToString(port, toPort), port.self.getChannel4Display());
        }
      }

    }

    //create connections between subfunctions
    for(let port of subports){
      if (!port.isIn){
        for (let toPort of port.self.getChannel4Display().getPort4DisplayList()){
          if (!this.portElkMap.has(toPort)){
            break;
          }
          let toPortElk = this.portElkMap.get(toPort);
          if (toPortElk.isIn && subports.includes(toPortElk)){
            connections.push([port, toPortElk]);
            this.connectionMap.set(this.portsToString(port, toPortElk), port.self.getChannel4Display())
          }
        }
      }
    }



    //Check for ambiguous connections. If a port has multiple incoming connections the layout is ambiguous, assuming the model is correct
    //If this is the case, we remove the duplicates and log a warning
    let toPortsSet = new Set(connections.map(c => c[1]));
    if (toPortsSet.size != connections.length){
      console.error('Ambiguous connections detected. The connections shown may differ from the modeled connections, but are semantically equivalent.');
      let unique: Port4Elk[] = [];
      let uniqueConnections: [Port4Elk,Port4Elk][] = [];
      connections.forEach(element => {
        if (!unique.includes(element[1])) {
          unique.push(element[1]);
          uniqueConnections.push(element);
        }
      });
      connections = uniqueConnections;
    }

    ret.ports = ports;
    ret.parts = subfunkElks;
    ret.connections = connections
    return ret;
  }

  //Gets all ports of a function and transforms them into Port4Elk
  //Also creates a mapping from Port4Display to Port4Elk by adding the Port4Elk to the portElkMap
  private getTransformedPorts(funk : Function4Display, elkfunk: Funktion4Elk) : Port4Elk[] {
    let ports : Port4Elk[] = [];
    if (funk.getInList() != null){
      ports.push(...funk.getInList().map(p => {
        let ret = {id:funk.getName() + '.' + p.getName(), isIn: true, funktion:elkfunk, self:p}
        this.portElkMap.set(p, ret);
        return ret;
      }));
    }
    if (funk.getOutList() != null) {
      ports.push(...funk.getOutList().map(p => {
        let ret = {id: funk.getName() + '.' + p.getName(), isIn: false, funktion: elkfunk, self: p}
        this.portElkMap.set(p, ret);
        return ret;
      }));
    }
    return ports;
  }

  public transform(part: Funktion4Elk){
    return {
      id: part.name,
      labels: [{
        text: part.name, height: 8, width: part.name.length * 7, layoutOptions: {
          ...(part.parts.length > 0 ? { 'elk.nodeLabels.placement': "[H_CENTER, V_TOP, INSIDE]" } : {})
        },
      }],
      ports: part.ports.map(p => ({
        layoutOptions: {
          'elk.port.side': p.isIn  ? 'WEST' : 'EAST',
        }, id: p.id, width: 20, height: 20, title: `${p.id}`, direction: p.isIn?'IN': 'OUT',
        labels: [{ text: p.id.split('.').pop(), height: 17.5, width: p.id.length * 7.4, hasChildren: part.parts.length > 0, }],
        port4display: p.self
      })),
      edges: part.connections.map(c => ({
        id: `${this.generateIDAndLinkToChannel(c[0], c[1])}`, sources: [c[0].id], targets: [c[1].id],
        channel4Display: c[0].self.getChannel4Display()
      })),
      children: part.parts.map(p => this.transform(p)),
      width: 240,
      height: 180,
      layoutOptions: {
        "elk.alignment": "TOP",
        ...(part.parts.length > 0 ? { 'elk.nodeLabels.placement': "[H_CENTER, V_TOP, INSIDE]" } : {})
      },
      funktion4display: part.self
    }
  }

  // Renders an ELK node as SVG
  public renderNode(node) {
    let svgNS = "http://www.w3.org/2000/svg";
    let g = document.createElementNS(svgNS, "g");

    // Component box
    if (!node.funktion4display.isVisible()) {
      return g;
    }
    g.setAttribute("transform", `translate(${node.x}, ${node.y})`);
    g.setAttribute("opacity", "1");
    let rect = document.createElementNS(svgNS, "rect");
    rect.setAttribute("id", `function-${node.id}`);
    rect.setAttribute("data-system", node.id);
    rect.setAttribute("x", "0");
    rect.setAttribute("y", "0");
    rect.setAttribute("width", `${node.width}`);
    rect.setAttribute("height", `${node.height}`);
    rect.setAttribute("fill", `${node.funktion4display.getBackgroundColor()}`);
    rect.setAttribute("stroke", `${node.funktion4display.getBorderColor()}`);
    rect.addEventListener('click', () => this.functionClicked(node.funktion4display));
    rect.addEventListener('mouseover', () => this.functionHovered(node.funktion4display));
    rect.addEventListener('mouseout', () => this.functionMouseouted(node.funktion4display));
    if (this.getSelectedFunctions().includes(node.funktion4display)){
      this.markElement(rect, node.funktion4display.getBorderColorHover());
    }
    rect.addEventListener('mouseover', () => {
      this.markElement(rect, node.funktion4display.getBorderColorHover());
    })
    rect.addEventListener('mouseout', () => {
      if (!this.getSelectedFunctions().includes(node.funktion4display)){
        this.unmarkElement(rect, node.funktion4display.getBorderColor());
      }
    })
    this.functionSvgMap.set(node.funktion4display, rect);
    g.appendChild(rect);

    // Component label
    for (let label of node.labels ?? []) {
      let text = document.createElementNS(svgNS, "text");
      text.setAttribute("x", `${label.x}`);
      text.setAttribute("y", `${label.y}`);
      text.setAttribute("font-size", "12");
      text.textContent = label.text;
      g.appendChild(text);
    }

    // Ports
    for (let port of node.ports ?? []) {
      if (!port.port4display.isVisible()) {
        continue;
      }
      let portG = document.createElementNS(svgNS, "g");
      portG.setAttribute("transform", `translate(${port.x}, ${port.y})`);
      let portRect = document.createElementNS(svgNS, "rect");
      portRect.setAttribute("class", "port");
      portRect.setAttribute("data-port", port.id);
      portRect.setAttribute("x", "0");
      portRect.setAttribute("y", "0");
      portRect.setAttribute("width", `${port.width}`);
      portRect.setAttribute("height", `${port.height}`);
      portRect.setAttribute("fill", `${port.port4display.getBackgroundColor()}`);
      portRect.setAttribute("stroke", `${port.port4display.getBorderColor()}`);
      portRect.addEventListener('click', () => this.portClicked(port.port4display));
      portRect.addEventListener('mouseover', () => this.portHovered(port.port4display));
      portRect.addEventListener('mouseout', () => this.portMouseouted(port.port4display));
      if (this.getSelectedPorts().includes(port.port4display)){
        this.markElement(portRect, port.port4display.getBorderColorHover());
      }
      portRect.addEventListener('mouseover', () => {
        this.markElement(portRect, port.port4display.getBorderColorHover());
      })
      portRect.addEventListener('mouseout', () => {
        if (!this.getSelectedPorts().includes(port.port4display)){
          this.unmarkElement(portRect, port.port4display.getBorderColor());
        }
      })
      this.portSvgMap.set(port.port4display, portRect);
      portG.appendChild(portRect);
      let title = document.createElementNS(svgNS, "title");
      title.textContent = port.title;
      portG.appendChild(title);
      let path = document.createElementNS(svgNS, "path");
      path.setAttribute("fill", "none");
      path.setAttribute("stroke", "rgb(0, 0, 0)");
      path.setAttribute("stroke-miterlimit", "10");
      path.setAttribute("pointer-events", "none");
      path.setAttribute("d", "M 0 -5 L 5 0 L 0 5 M 5 0 L -5 0");  //arrow
      path.setAttribute("transform", "translate(10, 10)");
      portG.appendChild(path);

      // Port label
      for (let label of port.labels ?? []) {
        if (!port.port4display.isLabelVisible()) {
          continue;
        }
        let text = document.createElementNS(svgNS, "text");
        text.setAttribute("x", `${label.x}`);
        text.setAttribute("y", `${label.hasChildren ? label.y : 14}`);
        text.setAttribute("font-family", "Menlo, Monaco, Courier New, monospace");
        text.setAttribute("font-size", "12");
        text.textContent = label.text;
        portG.appendChild(text);
      }
      g.appendChild(portG);
    }

    // Channels
    for (let edge of node.edges ?? []) {
      if (!edge.channel4Display.isVisible()) {
        continue;
      }
      for (let section of edge.sections) {
        let polyline = document.createElementNS(svgNS, "polyline");
        polyline.setAttribute("id", edge.id);
        polyline.setAttribute("points", `${section.startPoint.x},${section.startPoint.y} ${(section.bendPoints ?? []).map(p => `${p.x},${p.y}`).join(" ")} ${section.endPoint.x},${section.endPoint.y}`);
        polyline.setAttribute("stroke", `${edge.channel4Display.getLineColor()}`);
        polyline.setAttribute("stroke-width", "1");
        polyline.setAttribute("fill", "none");
        polyline.addEventListener('click', () => this.channelClicked(edge.channel4Display))
        polyline.addEventListener('mouseover', () => this.channelHovered(edge.channel4Display))
        polyline.addEventListener('mouseout', () => this.channelMouseouted(edge.channel4Display))
        if (this.getSelectedChannels().includes(edge.channel4Display)){
          this.markElement(polyline, edge.channel4Display.getLineColorHover());
        }
        g.appendChild(polyline);
        let hiddenPolyline = document.createElementNS(svgNS, "polyline");
        hiddenPolyline.setAttribute("points", `${section.startPoint.x},${section.startPoint.y} ${(section.bendPoints ?? []).map(p => `${p.x},${p.y}`).join(" ")} ${section.endPoint.x},${section.endPoint.y}`);
        hiddenPolyline.setAttribute("stroke", "transparent");
        hiddenPolyline.setAttribute("stroke-width", "15");
        hiddenPolyline.setAttribute("fill", "none");
        hiddenPolyline.addEventListener('mouseover', () => {
          this.markElement(polyline, edge.channel4Display.getLineColorHover());
        })
        hiddenPolyline.addEventListener('mouseout', () => {
          if (!this.getSelectedChannels().includes(edge.channel4Display)){
            this.unmarkElement(polyline, edge.channel4Display.getLineColor());
          }
        })
        hiddenPolyline.addEventListener('click', () => this.channelClicked(edge.channel4Display))
        hiddenPolyline.addEventListener('mouseover', () => this.channelHovered(edge.channel4Display))
        hiddenPolyline.addEventListener('mouseout', () => this.channelMouseouted(edge.channel4Display))
        g.appendChild(hiddenPolyline);
        let path = document.createElementNS(svgNS, "path");
        path.setAttribute("d", "M 0 0 L -7 -3.5 L -5.25 0 L -7 3.5 Z"); //arrow head
        path.setAttribute("transform", `translate(${section.endPoint.x - 1},${section.endPoint.y})`);
        path.setAttribute("fill", "rgb(0, 0, 0)");
        path.setAttribute("stroke", "rgb(0, 0, 0)");
        path.setAttribute("stroke-miterlimit", "10");
        g.appendChild(path);
      }
    }

    // Sub-components (recursive call)
    for (let child of node.children ?? []) {
      g.appendChild(this.renderNode(child));
    }
    return g;
  }



  public addOutsideClickListener(){
    let element = document.getElementById('function-' + FunctionDisplay.wrapperId);
    if (element) {
      element.addEventListener('click', () => this.outsideClicked())
    }
  }


  public functionClicked(f: Function4Display){
    if (this.getFunctionClick()){
      this.getFunctionClick()(f);
    }
  }

  public functionHovered(f: Function4Display){
    if (this.getFunctionMouseover()){
      this.getFunctionMouseover()(f);
    }
  }

  public functionMouseouted(f: Function4Display){
    if (this.getFunctionMouseout()){
      this.getFunctionMouseout()(f);
    }
  }

  public channelHovered(channel: Channel4Display){
    if (this.getChannelMouseover()){
      this.getChannelMouseover()(channel);
    }
  }

  public channelClicked(channel: Channel4Display){
    if (this.getChannelClick()){
      this.getChannelClick()(channel);
    }
  }

  public channelMouseouted(channel: Channel4Display){
    if (this.getChannelMouseout()){
      this.getChannelMouseout()(channel)
    }
  }

  private outsideClicked(){
    if (this.getOutsideClick()){
      this.getOutsideClick()();
    }
  }

  private portClicked(p: Port4Display){
    if (this.getPortClick()){
      this.getPortClick()(p)
    }
  }

  private portHovered(p: Port4Display){
    if (this.getPortMouseover()){
      this.getPortMouseover()(p);
    }
  }

  private portMouseouted(p: Port4Display){
    if (this.getPortMouseout()){
      this.getPortMouseout()(p)
    }
  }

  private generateIDAndLinkToChannel(p1: Port4Elk, p2: Port4Elk) {
    let id = this.generateID();
    this.channelIdMap.set(id, this.connectionMap.get(this.portsToString(p1, p2)));
    return id;
  }

  private portsToString(p1: Port4Elk, p2: Port4Elk) {
    return `${p1.id} -> ${p2.id}`;
  }

  private markElement(element: Element, markedColor: string) {
    element.setAttribute('stroke', markedColor);
    element.setAttribute('stroke-width', '5');
  }

  private unmarkElement(element: Element, unmarkedColor: string) {
    element.setAttribute('stroke', unmarkedColor);
    element.setAttribute('stroke-width', '1');
  }
}
