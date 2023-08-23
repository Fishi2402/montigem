import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { GemTextareaInput as GemTextareaInputTOP } from './gem-textarea-input/gem-textarea-input.component-gen';

@Component({
  selector: 'gem-textarea-input',
  templateUrl: './gem-textarea-input.component.html',
  styleUrls: ['./gem-textarea-input.component.scss']
})
export class GemTextareaInput extends GemTextareaInputTOP implements OnInit{

  // @Input() labelText: string;

  // @Input() textArea: string;

  // @Input() id: string;

  @Output() textAreaChange = new EventEmitter<string> ();

  onTextAreaChange(event: string) {
    this.textAreaChange.emit(event);
  }

  ngOnInit(): void {

  }



}
