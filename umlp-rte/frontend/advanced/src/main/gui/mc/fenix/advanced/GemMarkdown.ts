/* (c) https://github.com/MontiCore/monticore */

import { Component, HostBinding, OnChanges, SimpleChanges, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {
  config,
  GemMarkdownComponent,
} from '../../../../../../build/gui2java2ts/mc/fenix/advanced/GemMarkdownComponent';
import MarkdownIt from 'markdown-it';
import texmath from 'markdown-it-texmath';
import katex from 'katex';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  styleUrls: config.styleUrls,
  styles: config.styles,
  templateUrl: config.templateUrl,
  encapsulation: ViewEncapsulation.None,
})
export class GemMarkdown extends GemMarkdownComponent implements OnChanges {

  @HostBinding('style.width')
  _width: string = '100%';

  @HostBinding('style.height')
  _height: string = '100%';

  private md = new MarkdownIt({
    html: false,
    linkify: true,
    typographer: true,
    breaks: true
  }).use(texmath,{
    engine: katex,
    delimiters: 'dollars',
    texmath: {
      display: true
    },
    katexOptions: {
      throwOnError: false,
      strict: false,
      fleqn: true
    }
  });

  renderedHtml: SafeHtml = '';

  constructor(private sanitizer: DomSanitizer) {
    super();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['value']) {
      const rawHtml = this.md.render(this.value || '');

      this.renderedHtml = this.sanitizer.bypassSecurityTrustHtml(rawHtml);
    }
  }
}
