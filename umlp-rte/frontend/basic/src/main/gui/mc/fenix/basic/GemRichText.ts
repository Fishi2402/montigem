/* (c) https://github.com/MontiCore/monticore */

import {Component, HostBinding, OnChanges, SimpleChanges, ViewEncapsulation} from '@angular/core';
import {
  config,
  GemRichTextComponent,
} from '../../../../../../build/gui2java2ts/mc/fenix/basic/GemRichTextComponent';
import MarkdownIt from 'markdown-it';
import texmath from 'markdown-it-texmath';
import katex from 'katex';
//import 'katex/dist/katex.min.css';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: config.styles,
  templateUrl: config.templateUrl,
  encapsulation: ViewEncapsulation.None,
})
export class GemRichText extends GemRichTextComponent implements OnChanges {

  // Bind the width to the host element
  @HostBinding('style.width')
  _width: string = '100%';

  // Bind the height to the host element
  @HostBinding('style.height')
  _height: string = '100%';

  private md = new MarkdownIt({
    html: false,
    linkify: true,
    typographer: true
  }).use(texmath,{
    engine: katex,
    delimiters: 'dollars', // supports $...$ and $$...$$
    texmath: {
      display: true
    },
    katexOptions: {
      throwOnError: false,
      strict: false
    }
  });

  renderedHtml = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value']) {
      this.renderedHtml = this.md.render(this.value || '');
    }
  }
}
