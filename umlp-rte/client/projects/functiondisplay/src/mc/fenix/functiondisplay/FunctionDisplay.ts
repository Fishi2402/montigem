/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from "@angular/core";
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { config, FunctionDisplayComponent } from '../../../../build/gui/mc/fenix/functiondisplay/FunctionDisplayComponent';
import ELK, {ElkNode} from 'elkjs/lib/elk.bundled';
import { Function4Display } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Function4Display";
import { Function4DisplayBuilder } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Function4DisplayBuilder";
import { Port4Display } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Port4Display";
import { Channel4Display } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Channel4Display";
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

  public _svg: SafeHtml;

  private clickFunctionCallback : (f: Function4Display) => void;
  private overFunctionCallback  : (f: Function4Display) => void;
  private outFunctionCallback : (f: Function4Display) => void;
  private clickChannelCallback  : (c: Channel4Display) => void;
  private overChannelCallback  : (c: Channel4Display) => void;
  private outChannelCallback  : (c: Channel4Display) => void;
  private outsideClickCallback  : () => void;

  elk = new ELK();

  private elkdata :Funktion4Elk;

  private connectionMap = new Map<string, Channel4Display>();

  private channelIdMap = new Map<string, Channel4Display>();

  private static wrapperId = '----------------'

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
      //TODO Make this more secure
      this._svg = this.sanitizer.bypassSecurityTrustHtml(this.renderNode(v));
    })
      .then(() => new Promise(f => setTimeout(f, 100))
      ).then(() => {
      this.addChannelListeners();
      this.addFunctionListeners(this.elkdata.parts[0]);
      this.addOutsideClickListener();
    });
    layouted.catch(e => console.error(e));

  }

  @Input()
  public set functionClick(clickFunction: (f: Function4Display) => void){
    this.clickFunctionCallback = clickFunction;
  }
  @Input()
  public set functionMouseover(f: (f: Function4Display) => void){
    this.overFunctionCallback = f;
  }
  @Input()
  public set functionMouseout(f: (f: Function4Display) => void){
    this.outFunctionCallback = f;
  }
  @Input()
  public set channelClick(f: (c: Channel4Display) => void){
    this.clickChannelCallback = f;
  }
  @Input()
  public set channelMouseover(f: (c: Channel4Display) => void){
    this.overChannelCallback = f;
  }
  @Input()
  public set channelMouseout(f: (c: Channel4Display) => void){
    this.outChannelCallback = f;
  }
  @Input()
  public set outsideClick(f: () => void){
    this.outsideClickCallback = f;
  }


  ngOnInit() {
  }

  constructor(private sanitizer: DomSanitizer) {
    super();
  }

  private generateID (): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  private portMap = new Map<Port4Display, Port4Elk>();

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
    if(depth < 0){
      return {name: funk.getName(), parts: [], connections: [], ports:ports, self:funk};
    }
    let subports : Port4Elk[] = [];
    let subfunkElks : Funktion4Elk[] = [];

    //get all the subfunctions and their ports
    //calls itself recursively
    //Has to be done before creating the connections because connections require the portMap to be filled
    if(funk.getSubFunctionList() != null) {
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
        if (this.portMap.has(innerPort) && this.portMap.get(innerPort).isIn && subports.includes(this.portMap.get(innerPort))){
          connections.push([port, this.portMap.get(innerPort)]);
          this.connectionMap.set(this.portsToString(port, this.portMap.get(innerPort)), port.self.getChannel4Display());
        }
        //create connections from subfunctions to port
        if(this.portMap.has(innerPort) && !this.portMap.get(innerPort).isIn && subports.includes(this.portMap.get(innerPort))
          && (this.portMap.get(innerPort) != port)){
          connections.push([this.portMap.get(innerPort),port])
          this.connectionMap.set(this.portsToString(this.portMap.get(innerPort), port), port.self.getChannel4Display());
        }
      }
      //create direct connections between ports without a subfunction
      for(let toPort of ports){
        if(toPort != port && port.isIn && !toPort.isIn && toPort.self.getChannel4Display().getPort4DisplayList().includes(port.self)){
          connections.push([port,toPort]);
          this.connectionMap.set(this.portsToString(port, toPort), port.self.getChannel4Display());
        }
      }

    }

    //create connections between subfunctions
    for(let port of subports){
      if(!port.isIn){
        for (let toPort of port.self.getChannel4Display().getPort4DisplayList()){
          if(!this.portMap.has(toPort)){
            break;
          }
          let toPortElk = this.portMap.get(toPort);
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
    if(toPortsSet.size != connections.length){
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
  //Also creates a mapping from Port4Display to Port4Elk by adding the Port4Elk to the portMap
  private getTransformedPorts(funk : Function4Display, elkfunk: Funktion4Elk) : Port4Elk[] {
    let ports : Port4Elk[] = [];
    if(funk.getInList() != null){
      ports.push(...funk.getInList().map(p => {
        let ret = {id:funk.getName()+'.'+p.getName(), isIn: true, funktion:elkfunk, self:p}
        this.portMap.set(p, ret);
        return ret;
      }));
    }
    if(funk.getOutList() != null) {
      ports.push(...funk.getOutList().map(p => {
        let ret = {id: funk.getName()+'.'+p.getName(), isIn: false, funktion: elkfunk, self: p}
        this.portMap.set(p, ret);
        return ret;
      }));
    }
    return ports;
  }

  public transform(part: Funktion4Elk): ElkNode {
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
        }, id: p.id, width: 20, height: 20, title: `${p.id}`, direction: p.isIn?'IN': 'OUT', labels: [{ text: p.id.split('.').pop(), height: 17.5, width: p.id.length * 7.4, hasChildren: part.parts.length > 0, }]
      })),
      edges: part.connections.map(c => ({ id: `${this.generateIDAndLinkToChannel(c[0], c[1])}`, sources: [c[0].id], targets: [c[1].id] })),
      children: part.parts.map(p => this.transform(p)),
      width: 240,
      height: 180,
      layoutOptions: {
        "elk.alignment": "TOP",
        ...(part.parts.length > 0 ? { 'elk.nodeLabels.placement': "[H_CENTER, V_TOP, INSIDE]" } : {})
      },
    }
  }

  // Renders an ELK node as SVG
  public renderNode(node)   {
    let output = `<g transform="translate(${node.x}, ${node.y})" opacity="1">`;
    output += `<rect id="function-${node.id}" data-system="${node.id}" x="0" y="0" width="${node.width}" height="${node.height}" fill="white" stroke="black""></rect>`;
    for (let label of node.labels ?? []) {
      output += `<text x="${label.x}" y="${label.y}" font-size="12">${label.text}</text>`;
    }
    for (let port of node.ports ?? []) {
      output += `<g transform="translate(${port.x}, ${port.y})">`;
      output += `<rect class="port" data-port="${port.id}" x="0" y="0" width="${port.width}" height="${port.height}" fill="white" stroke="black"></rect>`;
      output += `<title>${port.title}</title>`
      output += `<path fill="none" stroke="rgb(0, 0, 0)" stroke-miterlimit="10" pointer-events="none" d="M 0 -5 L 5 0 L 0 5 M 5 0 L -5 0" transform="translate(10, 10)"></path>`
      for (let label of port.labels ?? []) {
        output += `<text x="${label.x}" y="${label.hasChildren ? label.y : 14}" font-family="Menlo, Monaco, Courier New, monospace" font-size="12">${label.text}</text>`;
      }
      output += "</g>";
    }
    for (let edge of node.edges ?? []) {
      for (let section of edge.sections) {
        output += `<polyline id="${edge.id}" points="${section.startPoint.x},${section.startPoint.y} ${(section.bendPoints ?? []).map(p => `${p.x},${p.y}`).join(" ")} ${section.endPoint.x},${section.endPoint.y}" stroke="black" fill="none"></polyline>`;
        output += `<path d="M 0 0 L -7 -3.5 L -5.25 0 L -7 3.5 Z" transform="translate(${section.endPoint.x - 1},${section.endPoint.y})" fill="rgb(0, 0, 0)" stroke="rgb(0, 0, 0)" stroke-miterlimit="10"></path>`;
      }
    }
    for (let child of node.children ?? []) {
      output += this.renderNode(child);
    }
    output += "</g>";
    return output;
  }

  public addFunctionListeners(f: Funktion4Elk){
    const element  = document.getElementById(`function-${f.name}`)
    if(element){
      element.addEventListener('click', () => this.functionClicked(f));
      element.addEventListener('mouseover', () => this.functionHovered(f));
      element.addEventListener('mouseout', () => this.functionMouseouted(f));
      element.addEventListener('mouseover', () => {
        element.style.stroke = 'red'
        element.style.strokeWidth = '5'
      })
      element.addEventListener('mouseout', () => {
        element.style.stroke = 'black'
        element.style.strokeWidth = '1'
      })
    }
    f.parts.forEach(f => this.addFunctionListeners(f));
  }

  public addChannelListeners(){
    this.channelIdMap.forEach((channel, id) => {
      let element = document.getElementById(id);
      if(element){
        element.addEventListener('click', () => this.channelClicked(channel))
        element.addEventListener('mouseover', () => this.channelHovered(channel))
        element.addEventListener('mouseout', () => this.channelMouseouted(channel))
        element.addEventListener('mouseover', () => {
          element.style.stroke = 'red'
          element.style.strokeWidth = '5'
        })
        element.addEventListener('mouseout', () => {
          element.style.stroke = 'black'
          element.style.strokeWidth = '1'
        })
      }
    })
  }

  public addOutsideClickListener(){
    let element = document.getElementById('function-'+FunctionDisplay.wrapperId);
    if(element) {
      element.addEventListener('click', () => this.outsideClicked())
    }
  }

  public functionClicked(f: Funktion4Elk){
    if(this.clickFunctionCallback){
      this.clickFunctionCallback(f.self)
    }
  }

  public functionHovered(f: Funktion4Elk){
    if(this.overFunctionCallback){
      this.overFunctionCallback(f.self)
    }
  }

  public functionMouseouted(f: Funktion4Elk){
    if(this.outFunctionCallback){
      this.outFunctionCallback(f.self)
    }
  }

  public channelHovered(channel: Channel4Display){
    if(this.overChannelCallback){
      this.overChannelCallback(channel)
    }
  }

  public channelClicked(channel: Channel4Display){
    if(this.clickChannelCallback){
      this.clickChannelCallback(channel)
    }
  }

  public channelMouseouted(channel: Channel4Display){
    if(this.outChannelCallback){
      this.outChannelCallback(channel)
    }
  }

  private outsideClicked(){
    if(this.outsideClickCallback){
      this.outsideClickCallback()
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
}
