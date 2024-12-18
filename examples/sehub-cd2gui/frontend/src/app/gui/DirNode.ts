/* (c) https://github.com/MontiCore/monticore */

import {DirNodeComponent, config} from '@src/gui/DirNodeComponent';
import {Component} from '@angular/core';
import {Artifact} from "@src/sehub/Artifact";
import {Directory} from "@src/sehub/Directory";
import { GemtreenodeModule } from "@umlp/arrange";
import { GemtextModule } from "@umlp/basic";
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
    return (art as Directory).getContainsList();
  }

}


