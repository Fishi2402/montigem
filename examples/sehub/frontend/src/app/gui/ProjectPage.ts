/* (c) https://github.com/MontiCore/monticore */

import {ProjectPageComponent,config} from 'gui/ProjectPageComponent';
import {Artifact} from 'sehub/Artifact';
import {Directory} from 'sehub/Directory';
import { SehubSecurityManager } from "../sehub/SehubSecurityManager";
import {Component} from '@angular/core';

import {
  GraphNode,
  GraphEdge,
  GemGraph,
  GraphEdgeBuilder,
  GraphNodeBuilder,
  Graph,
  GraphBuilder,
} from '@umlp/networkgraph';
@Component({
  standalone:true,
  imports:[...config.imports,
    GemGraph],
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

  public getGemGraphEdges() : GraphEdge[] {
    return this.calculateNetwork().edges
  }


  public getGemGraphNodes(): GraphNode[] {
    return this.calculateNetwork().nodes
  }

  /*
  public override getProjectGraphGraph(): Graph {
    return new GraphBuilder()
    .edges(this.getGemGraphEdges())
    .nodes(this.getGemGraphNodes())
    .build();
  }
  */

  // TODO: Fix circular structures in `networkData`
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
      dir.getContainsList().forEach(a => edges.push(this.createEdge(dir.getFullName(), a.getFullName())))
    })
    return edges;
  }

  private createEdge(start : string, end: string): GraphEdge {
    return new GraphEdgeBuilder().source(start).target(end).build();
  }

  private getNodesFromArtifacts(artifacts:Artifact[]): GraphNode[] {
    return artifacts.map(a => this.getNodeFromArtifact(a));
  }

  private getAllArtifactsFromDir(art: Artifact, artifacts: Artifact[]): Artifact []{
    if(art instanceof Directory) {
      (art as Directory).getContainsList().forEach(a => this.getAllArtifactsFromDir(a, artifacts))
    }
    artifacts.push(art)
    return artifacts;
  }

  private getNodeFromArtifact(art : Artifact): GraphNode {
    return new GraphNodeBuilder()
        .id(art.getFullName())
        .color((art instanceof Directory)? "#a9dcd8" : "#c2f8cd")
        .size(50)
        .label(art.getName()).build();
  }

}


