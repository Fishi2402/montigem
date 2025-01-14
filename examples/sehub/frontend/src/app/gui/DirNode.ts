/* (c) https://github.com/MontiCore/monticore */

import {DirNodeComponent, config} from 'gui/DirNodeComponent';
import {Component} from '@angular/core';
import {Artifact} from "sehub/Artifact";
import {Directory} from "sehub/Directory";
import { CommonModule } from "@angular/common";

@Component({
  standalone:true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class DirNode extends DirNodeComponent {

  public override isDirectory(): boolean {
    return this.getArtifact() instanceof Directory;
  }

  public override getChildren(art: Artifact): Array<Artifact> {
    if (this.isDirectory()) {
      return (art as Directory).getContainsList();
    }
    return null;
  }

}


