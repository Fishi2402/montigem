/* (c) https://github.com/MontiCore/monticore */
import { config, MonacoEditorComponent } from '../../../../../../build/gui2java2ts/mc/fenix/editor/MonacoEditorComponent';
import {EventEmitter, Output, Provider, ElementRef, AfterViewInit, OnDestroy} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {MonacoEditorModule, NGX_MONACO_EDITOR_CONFIG, NgxMonacoEditorConfig } from 'ngx-monaco-editor-v2';
import * as monacoTypes from "monaco-editor";

// This is necessary so we use the same static functions as the ngx monaco editor instance
// See: https://stackoverflow.com/questions/79536928/cannot-make-the-model-markers-to-appear-in-monaco-editor-inside-angular-compone
declare var monaco: typeof monacoTypes;

export function provideMonacoEditor(config: NgxMonacoEditorConfig = {}): Provider[] {
  return [
    { provide: NGX_MONACO_EDITOR_CONFIG, useValue: config }
  ];
}

import {
  Component,
} from '@angular/core';
import {InlineDecoration} from "../../../../../../build/gui2java2ts/mc/fenix/editor/monacoeditortypes/InlineDecoration";
import {HoverContent} from "../../../../../../build/gui2java2ts/mc/fenix/editor/monacoeditortypes/HoverContent";

@Component({
  standalone:true,
  imports:[...config.imports, MonacoEditorModule, FormsModule],
  selector: config.selector,
  providers: [
    provideMonacoEditor ({
      baseUrl: 'assets',
      defaultOptions: {
        scrollBeyondLastLine: false,
        theme: 'vs-dark'
      }
    })
  ],
  templateUrl: "MonacoEditor.html",
  styleUrls: ["MonacoEditor.scss"]
})

export class MonacoEditor extends MonacoEditorComponent implements AfterViewInit, OnDestroy {

  protected editor: monacoTypes.editor.ICodeEditor;
  protected cacheCheck : string = "";
  protected highlightingCache;
  private resizeObserver: ResizeObserver;

  /**
   * Adapt the component here if you want to add any options
   */
  public _options : any = {
    value: this._entry,
    language: this._language,
    theme: this._theme,
    readOnly: this._disabled,
    glyphMargin: this._glyphMargin,
    automaticLayout: false,
    hover: {
      enabled: true,
      sticky: true,
      above: false
    }
  };

  public constructor(private elementRef: ElementRef) {
    setTimeout( () =>
      monaco.languages.registerHoverProvider("*", {
        provideHover: (model,position) =>  {
          if(this.getHoverContents()) {
            for(let hC of this.getHoverContents()) {
              if((position.lineNumber > hC.getRange().getStartLin() ||
                (position.lineNumber === hC.getRange().getStartLin() && position.column >= hC.getRange().getStartCol())) &&
                (position.lineNumber < hC.getRange().getEndLin() ||
                  position.lineNumber === hC.getRange().getEndLin() && position.column <= hC.getRange().getEndCol())) {
                return {
                  contents:
                    hC.getHoverContentList().map(c => {
                      return {
                        value: c
                      };
                    })
                };
              }
            }
          }
          return {
            contents: [
              { value: ''}
            ]
          };
        }
      }), 1000)

    super();
  }

  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver(() => {
      if (this.editor) {
        this.editor.layout();
      }
    });
    this.resizeObserver.observe(this.elementRef.nativeElement);
  }

  ngOnDestroy() {
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
    }
    super.ngOnDestroy();
  }

  @Output() public entryChange = new EventEmitter<string>();

  onEntryChange(event: string) {
    // console.warn("Entry change")
    this.entryChange.emit(event);
    if(this.editor) {
      this.highlightInitialization(this.editor)
      this.scrollToLine(this.editor)
    }
  }

  public saveEditor(editorInstance: monacoTypes.editor.IStandaloneCodeEditor) : void {
    this.editor = editorInstance;
    if(this.getHighlighting()) {
      this.highlightInitialization(editorInstance)
      this.scrollToLine(this.editor)
    }
    // Initial layout
    setTimeout(() => this.editor.layout(), 0);
  }

  public highlightInitialization(editor: monacoTypes.editor.ICodeEditor): void {
    // console.warn("Highlighting now", this.getHighlighting())
    if(!this.getHighlighting()) {
      return;
    }
    const highlighting = this.getHighlighting().map(inlineHighlight => {
      // console.warn("Highlighting now! ",  inlineHighlight.getCssClass())
      return {
        range: {
          startLineNumber: inlineHighlight.getRange().getStartLin(),
          startColumn: inlineHighlight.getRange().getStartCol(),
          endLineNumber: inlineHighlight.getRange().getEndLin(),
          endColumn: inlineHighlight.getRange().getEndCol(),
        },
        options: {
          inlineClassName: inlineHighlight.getCssClass()
        }
      };
    });

    const cache = JSON.stringify(highlighting);

    if(cache === this.highlightingCache) {
      return;
    } {
      // console.warn("This is called ", highlighting)
      this.cacheCheck = cache;
      this.highlightingCache = highlighting;
      editor.createDecorationsCollection(this.highlightingCache);
    }
  }

  public scrollToLine(editor: monacoTypes.editor.ICodeEditor): void {
    // console.warn("Scroll to line now", this.getInitialScrollToLine())
    if(!this.getInitialScrollToLine()) {
      return;
    }
    editor.revealLineNearTop(this.getInitialScrollToLine())
  }

  public override setLanguage(val: string) {
    // console.warn("Setting language", val)
    super.setLanguage(val);
    this.updateOptions();
  }

  public override setTheme(val: string) {
    // console.warn("Setting theme")
    super.setTheme(val);
    this.updateOptions();
  }

  public override setDisabled(val: boolean) {
    // console.warn("Setting disabled")
    super.setDisabled(val);
    this.updateOptions();
  }

  public override setHoverContents(val: HoverContent[]) {
    // console.warn("Trying to set hover : ",val)
    super.setHoverContents(val)
    if(!val) {
      return;
    }
  }

  public override setHighlighting(val: InlineDecoration[]) {
    // console.warn("Setting highlighting")
    super.setHighlighting(val);
    if(this.editor) {
      this.highlightInitialization(this.editor);
    } else {
      // console.warn("Highlighting initialization not possible due to editor not set.")
    }
  }

  public updateOptions() : void {
    // console.warn("Updating options")
    this._options = {
      value: this._entry,
      language: this._language,
      theme: this._theme,
      readOnly: this._disabled,
      glyphMargin: this._glyphMargin,
      automaticLayout: false,
      hover: {
        enabled: true,
        sticky: true,
        above: false
      }
    };
  }
}
