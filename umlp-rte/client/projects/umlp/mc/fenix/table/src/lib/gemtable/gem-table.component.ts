/* (c) https://github.com/MontiCore/monticore */

import {Component, HostBinding, Input, TemplateRef} from "@angular/core";

@Component({
  selector: "gem-table",
  templateUrl: "./gem-table.component.html",
  styleUrls: ["./gem-table.component.scss"],
})
export class GemTable {
  @HostBinding("attr.id")
  @Input() id: string;

  @Input() width: string = "100%";
  @Input() header: TemplateRef<any>;
  @Input() body: TemplateRef<any>;
  @Input() caption: string;
}
