/* (c) https://github.com/MontiCore/monticore */
/* adapted and customized by (c) MaCoCo, ein RWTH Aachen projekt */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['../../../../theme/src/dialog.component.scss'],
})
export class DialogComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit() { }
}
