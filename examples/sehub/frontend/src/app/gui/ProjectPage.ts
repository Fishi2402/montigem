/* (c) https://github.com/MontiCore/monticore */

import {GemTreeviewEntry} from '@umlp/basic';
import {ProjectPageComponent,config} from '@src/gui/ProjectPageComponent';
import {Artifact} from '@src/sehub/Artifact';
import {Directory} from '@src/sehub/Directory';
import { SehubSecurityManager } from "../sehub/SehubSecurityManager";
import {Component} from '@angular/core';

import { GraphNode, GraphEdge } from '@umlp/networkgraph';
@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class ProjectPage extends ProjectPageComponent {

  networkDataCheck = { value: null, hash: null, calculating: false };
  treeViewDataCheck = { value: null, hash: null, calculating: false };

  ngOnInit(){
    if (!SehubSecurityManager.isSignedIn()) {
      this._router.navigate(['gui/login/LoginPage']);
    }
    super.ngOnInit();
  }

  public override getGemGraph1Edges() : GraphEdge[] {
    return this.calculateNetwork().edges
  }


  public override getGemGraph1Nodes(): GraphNode[] {
    return this.calculateNetwork().nodes
  }

  public calculateNetwork(): {nodes: GraphNode[], edges:GraphEdge[]}  {
    if(!(this.project)) {
      return null;
    }
    const networkData = {nodes : [], edges : []}

    const artifacts : Artifact[] = this.getAllArtifactsFromDir(this.project, []);

    networkData.nodes = this.getNodesFromArtifacts(artifacts);
    networkData.edges = this.getEdgesFromArtifacts(artifacts);
    const newHash = JSON.stringify(networkData);
    if(this.networkDataCheck.hash !== newHash) {
      this.networkDataCheck.hash = newHash;
      this.networkDataCheck.value = networkData;
    }
    return this.networkDataCheck.value;
  }


  private getEdgesFromArtifacts(artifacts:Artifact[]) : GraphEdge[] {
    let edges : GraphEdge[] = [];
    artifacts.filter(a => a instanceof Directory).forEach((dir : Directory) => {
      dir.getContains().forEach(a => edges.push(this.createEdge(dir.getFullName(), a.getFullName())))
    })
    return edges;
  }

  private createEdge(start : string, end: string): GraphEdge {
    return {
      from: start,
      to: end
    }
  }

  private getNodesFromArtifacts(artifacts:Artifact[]): GraphNode[] {
    return artifacts.map(a => this.getNodeFromArtifact(a));
  }

  private getAllArtifactsFromDir(art: Artifact, artifacts: Artifact[]): Artifact []{
    if(art instanceof Directory) {
      (art as Directory).getContains().forEach(a => this.getAllArtifactsFromDir(a, artifacts))
    }
    artifacts.push(art)
    return artifacts;
  }

  private getNodeFromArtifact(art : Artifact): GraphNode {
    return {
      id : art.getFullName(),
      color : (art instanceof Directory)? "#a9dcd8" : "#c2f8cd",
      mass : 50,
      label : art.getName()
    }
  }

  public getFilesItems(): Array<GemTreeviewEntry> {
    if(!(this.project)) {
      return [];
    }
    const treeViewEntries = [this.recursivelyFillGemTreeviewItems(this.project)]

    const newHash = JSON.stringify(treeViewEntries);
    if(this.treeViewDataCheck.hash !== newHash) {
      this.treeViewDataCheck.hash = newHash;
      this.treeViewDataCheck.value = treeViewEntries;
    }
    return this.treeViewDataCheck.value
  }

  private recursivelyFillGemTreeviewItems(artifact:Artifact): GemTreeviewEntry {
    let curViewEntry : GemTreeviewEntry = this.createGemTreeviewEntryFromArtifact(artifact)
    if(artifact instanceof Directory) {
      (artifact as Directory).getContains().forEach(art => curViewEntry.children.push(this.recursivelyFillGemTreeviewItems(art)))
    }
    return curViewEntry;
  }

  private createGemTreeviewEntryFromArtifact(artifact:Artifact): GemTreeviewEntry {
    const viewEntry = new GemTreeviewEntry();
    viewEntry.expanded = true
    viewEntry.children = []
    viewEntry.name = artifact.getName()

    return viewEntry
  }
}


