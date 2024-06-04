/* (c) https://github.com/MontiCore/monticore */

import { GemDialogExampleComponent, config } from '@src/example_arrange/GemDialogExampleComponent';
import { Component } from '@angular/core';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styleUrls: config.styleUrls
})
export class GemDialogExample extends GemDialogExampleComponent {
  public override openDialog(): (arg: void) => void {
    return $ => this.dialogOpen = true;
  }

  public override getDialogIsOpen(): boolean {
    return this.isDialogOpen();
  }

  public override setDialogIsOpen(val: boolean): void {
    this.dialogOpen = val;
  }
}
