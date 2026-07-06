/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {PersonEditComponent,config} from 'stanley/personedit/PersonEditComponent';
import {Person} from 'hardy/flensburg/Person';
import { Event } from '@umlp/commonj2ts';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class PersonEdit extends PersonEditComponent {

  public  decreaseAge (p: Person): Event<void> {
    return null;
  }

}
