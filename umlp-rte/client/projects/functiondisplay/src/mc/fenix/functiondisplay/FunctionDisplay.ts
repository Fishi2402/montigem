/* (c) https://github.com/MontiCore/monticore */

import { Component, Input } from "@angular/core";
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { config, FunctionDisplayComponent } from '../../../../build/gui/mc/fenix/functiondisplay/FunctionDisplayComponent';
import ELK from 'elkjs/lib/elk.bundled';
import { Function4Display } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Function4Display";
import { Function4DisplayBuilder } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Function4DisplayBuilder";
import { Port4Display } from "../../../../build/cd/mc/fenix/functiondisplay/functionstructure4fenix/Port4Display";
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

  elk = new ELK();

  @Input()
  public set data(data: Function4Display) {
    console.log(data);
    if (data == null) { return; }
    this._data = data;
    let elkdata = this.funk2elkfunk(this.addWrapper(data));
    console.log(elkdata);
    let transformed = this.transform(elkdata);
    console.log(transformed);
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
        "elk.alignment": "LEFT", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-alignment.html
        "elk.padding": "[top=30,left=30,bottom=30,right=30]", // padding to child nodes https://eclipse.dev/elk/reference/options/org-eclipse-elk-padding.html
        //"elk.layered.nodePlacement.favorStraightEdges": false, // https://eclipse.dev/elk/reference/options/org-eclipse-elk-layered-nodePlacement-favorStraightEdges.html
        "elk.layered.nodePlacement.strategy": "SIMPLE", // https://eclipse.dev/elk/reference/options/org-eclipse-elk-layered-nodePlacement-strategy.html
      }
    });
    layouted.then(v => {
      console.error(v)
      this._svg = this.sanitizer.bypassSecurityTrustHtml(this.renderNode(v));
      console.log(this._svg);
      console.log(v);
    });
    layouted.catch(e => console.error(e));

  }

  ngOnInit() {
    console.log(this.elk);
  }

  constructor(private sanitizer: DomSanitizer) {
    super();
  }

  private generateID(): string {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  }

  private portMap = new Map<Port4Display, Port4Elk>();

  private addWrapper(funk: Function4Display): Function4Display {
    return new Function4DisplayBuilder()
      .name(this.generateID())
      .subFunction([funk])
      .owner(undefined)
      .build();
  }

  private funk2elkfunk(funk: Function4Display): Funktion4Elk {
    let ret: Funktion4Elk = { name: funk.getName(), parts: null, connections: null, ports: null, self: funk };
    let ports: Port4Elk[] = this.getTransformedPorts(funk, ret);

    let subports: Port4Elk[] = [];
    let subfunkElks: Funktion4Elk[] = [];
    if (funk.getSubFunctionList() != null) {
      for (let sf of funk.getSubFunctionList()) {
        let sfelk = this.funk2elkfunk(sf);
        subfunkElks.push(sfelk);
        subports.push(...this.getTransformedPorts(sf, sfelk));
      }
    }

    let connections: [Port4Elk, Port4Elk][] = [];
    for (let port of ports) {
      for (let toPort of port.self.getChannel4Display().getPort4DisplayList()) {
        if (this.portMap.has(toPort) && this.portMap.get(toPort).isIn && subports.includes(this.portMap.get(toPort))) {
          connections.push([port, this.portMap.get(toPort)]);
        }
        if (this.portMap.has(toPort) && !this.portMap.get(toPort).isIn && ports.includes(this.portMap.get(toPort))
          && (this.portMap.get(toPort) != port)) {
          connections.push([port, this.portMap.get(toPort)])
        }
      }

    }

    for (let port of subports) {
      if (!port.isIn) {
        for (let toPort of port.self.getChannel4Display().getPort4DisplayList()) {
          if (!this.portMap.has(toPort)) {
            break;
          }
          let toPortElk = this.portMap.get(toPort);
          if (toPortElk.isIn && subports.includes(toPortElk)) {
            connections.push([port, toPortElk]);
          }
          if (!toPortElk.isIn && ports.includes(toPortElk)) {
            connections.push([port, toPortElk])
          }
        }
      }
    }

    ret.ports = ports;
    ret.parts = subfunkElks;
    ret.connections = connections
    return ret;
  }

  private getTransformedPorts(funk: Function4Display, elkfunk: Funktion4Elk): Port4Elk[] {
    let ports: Port4Elk[] = [];
    if (funk.getInList() != null) {
      ports.push(...funk.getInList().map(p => {
        let ret = { id: p.getName(), isIn: true, funktion: elkfunk, self: p }
        this.portMap.set(p, ret);
        return ret;
      }));
    }
    if (funk.getOutList() != null) {
      ports.push(...funk.getOutList().map(p => {
        let ret = { id: p.getName(), isIn: false, funktion: elkfunk, self: p }
        this.portMap.set(p, ret);
        return ret;
      }));
    }
    return ports;
  }

  public transform(part: Funktion4Elk): any {
    return {
      id: part.name,
      labels: [{
        text: part.name, height: 8, width: part.name.length * 7, layoutOptions: {
          ...(part.parts.length > 0 ? { 'elk.nodeLabels.placement': "[H_CENTER, V_TOP, INSIDE]" } : {})
        },
      }],
      ports: part.ports.map(p => ({
        layoutOptions: {
          'elk.port.side': p.isIn ? 'WEST' : 'EAST',
        }, id: p.id, width: 20, height: 20, title: `${p.id}`, direction: p.isIn ? 'IN' : 'OUT', labels: [{ text: p.id, height: 17.5, width: p.id.length * 7.4, hasChildren: part.parts.length > 0, }]
      })),
      edges: part.connections.map(c => ({ id: this.generateID(), sources: [c[0].id], targets: [c[1].id] })),
      children: part.parts.map(p => this.transform(p)),
      width: 240,
      height: 180,
      layoutOptions: {
        "elk.alignment": "TOP",
        ...(part.parts.length > 0 ? { 'elk.nodeLabels.placement': "[H_CENTER, V_TOP, INSIDE]" } : {})
      },
    }
  }

  public renderNode(node) {
    let output = `<g transform="translate(${node.x}, ${node.y})" opacity="1">`;
    output += `<rect data-system="${node.id}" x="0" y="0" width="${node.width}" height="${node.height}" fill="white" stroke="black"></rect>`;
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
        output += `<polyline points="${section.startPoint.x},${section.startPoint.y} ${(section.bendPoints ?? []).map(p => `${p.x},${p.y}`).join(" ")} ${section.endPoint.x},${section.endPoint.y}" stroke="black" fill="none"></polyline>`;
        output += `<path d="M 0 0 L -7 -3.5 L -5.25 0 L -7 3.5 Z" transform="translate(${section.endPoint.x - 1},${section.endPoint.y})" fill="rgb(0, 0, 0)" stroke="rgb(0, 0, 0)" stroke-miterlimit="10"></path>`;
      }
    }
    for (let child of node.children ?? []) {
      output += this.renderNode(child);
    }
    output += "</g>";
    return output;
  }



}