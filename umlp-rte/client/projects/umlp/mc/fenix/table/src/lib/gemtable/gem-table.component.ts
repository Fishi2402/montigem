/* (c) https://github.com/MontiCore/monticore */

import {Component, HostBinding, Input, TemplateRef} from "@angular/core";

@Component({
  selector: "gem-table",
  templateUrl: "./gem-table.component.html",
})
export class GemTable {
  @HostBinding("style.width")
  @Input() width: string;

  @HostBinding("style.height")
  @Input() height: string;

  @HostBinding("style.overflow")
  overflow: string = "auto";

  @Input() tableLayout: string;
  @Input() headers: TemplateRef<any>[];
  @Input() body: TemplateRef<any>[];
}
