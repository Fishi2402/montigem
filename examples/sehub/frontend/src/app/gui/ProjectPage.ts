/* (c) https://github.com/MontiCore/monticore */

import { Artifact } from '@src/sehub/Artifact';
import { GemNetworkData, GemNetworkNode, GemNetworkEdge } from '@monticore/montigem-charts';
import {  } from '@monticore/montigem-charts';
import { ProjectPageTOP } from '@src/gui/ProjectPageTOP';
import { GemTreeviewEntry } from '@monticore/montigem-basic';
import { Directory } from '@src/sehub/Directory';

import {
  Component,
} from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'project-page',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/ProjectPage.html',
  styles: [":host{ display: flex; flex-direction: column }"],
  styleUrls: []
})

export class ProjectPage extends ProjectPageTOP {

  networkDataCheck = { value: null, hash: null, calculating: false };
  treeViewDataCheck = { value: null, hash: null, calculating: false };

  public getGemNetworkGraph1Data(): GemNetworkData  {
    if(!(this.project && this.project.getRootArtifact()[0])) {
      return null;
    }
    const networkData = new GemNetworkData();
    const rootDir : Directory = this.project.getRootArtifact()[0];

    const artifacts : Artifact[] = this.getAllArtifactsFromDir(rootDir, []);

    networkData.nodes = this.getNodesFromArtifacts(artifacts);
    networkData.edges = this.getEdgesFromArtifacts(artifacts);
    const newHash = JSON.stringify(networkData);
    if(this.networkDataCheck.hash !== newHash) {
      this.networkDataCheck.hash = newHash;
      this.networkDataCheck.value = networkData;
    }
    return this.networkDataCheck.value;
  }


  private getEdgesFromArtifacts(artifacts:Artifact[]) : GemNetworkEdge[] {
    let edges : GemNetworkEdge[] = [];
    artifacts.filter(a => a instanceof Directory).forEach((dir : Directory) => {
      dir.getContains().forEach(a => edges.push(this.createEdge(dir.getFullName(), a.getFullName())))
    })
    return edges;
  }

  private createEdge(start : string, end: string): GemNetworkEdge {
    const edge = new GemNetworkEdge();
    edge.from = start;
    edge.to = end;
    edge.length = 1.0;
    return edge;
  }

  private getNodesFromArtifacts(artifacts:Artifact[]): GemNetworkNode[] {
    return artifacts.map(a => this.getNodeFromArtifact(a));
  }

  private getAllArtifactsFromDir(art: Artifact, artifacts: Artifact[]): Artifact []{
    if(art instanceof Directory) {
      (art as Directory).getContains().forEach(a => this.getAllArtifactsFromDir(a, artifacts))
    }
    artifacts.push(art)
    return artifacts;
  }

  private getNodeFromArtifact(art : Artifact): GemNetworkNode {
    let node : GemNetworkNode = new GemNetworkNode();
    node.id = art.getFullName()
    node.color = (art instanceof Directory)? "#FFFF00" : "#FF00BF";
    node.mass = 1.0;

    return node;
  }

  public getFilesItems(): Array<GemTreeviewEntry> {
    if(!(this.project && this.project.getRootArtifact()[0])) {
      return [];
    }
    const rootDir : Directory = this.project.getRootArtifact()[0];
    const treeViewEntries = [this.recursivelyFillGemTreeviewItems(rootDir)]

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


