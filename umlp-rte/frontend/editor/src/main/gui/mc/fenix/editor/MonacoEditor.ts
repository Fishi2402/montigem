/* (c) https://github.com/MontiCore/monticore */
import { config, MonacoEditorComponent } from '../../../../../../build/gui2java2ts/mc/fenix/editor/MonacoEditorComponent';
import {EventEmitter, Output, Provider, ElementRef, AfterViewInit, OnDestroy, HostBinding} from '@angular/core';
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

  @HostBinding('style.width') get styleWidth() { return this.getWidth(); }
  @HostBinding('style.height') get styleHeight() {
      if (this.isAutoHeight()) return 'auto';
      return this.getHeight();
  }

  protected editor: monacoTypes.editor.ICodeEditor;
  protected cacheCheck : string = "";
  protected highlightingCache;
  private resizeObserver: ResizeObserver;
  private contentSizeListener: monacoTypes.IDisposable;

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
    setTimeout(() => {
      monaco.languages.registerHoverProvider("*", {
        provideHover: async (model, position, token) => {
          const hoverContents = this.getHoverContents();
          if (!hoverContents) {
            return null;
          }
          for (const hC of hoverContents) {
            const range = hC.getRange();
            const isInRange =
              (position.lineNumber > range.getStartLin() ||
                (position.lineNumber === range.getStartLin() &&
                  position.column >= range.getStartCol())) &&
              (position.lineNumber < range.getEndLin() ||
                (position.lineNumber === range.getEndLin() &&
                  position.column <= range.getEndCol()));
            if (!isInRange) {
              continue;
            }
            const contents: { value: string }[] = [];
            for await (const content of hC.getHoverContentList()) {
              if (token.isCancellationRequested) {
                return null;
              }
              contents.push({ value: content });
            }
            return { contents };
          }
          return null;
        }
      });
    }, 1000);

    super();
  }

  ngAfterViewInit() {
    this.resizeObserver = new ResizeObserver(() => {
      if (this.editor && !this.isAutoHeight()) {
        this.editor.layout();
      }
    });
    this.resizeObserver.observe(this.elementRef.nativeElement);
  }

  ngOnDestroy() {
    if (this.resizeObserver) {
      this.resizeObserver.disconnect();
    }
    if (this.contentSizeListener) {
        this.contentSizeListener.dispose();
    }
    super.ngOnDestroy();
  }

  @Output() public entryChange = new EventEmitter<string>();

  onEntryChange(event: string) {
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

    if (this.isAutoHeight()) {
        this.updateHeight();
        this.contentSizeListener = this.editor.onDidContentSizeChange(() => {
            this.updateHeight();
        });
    }

    // Initial layout
    setTimeout(() => this.editor.layout(), 0);
  }

  private updateHeight() {
      if (!this.editor) return;
      const contentHeight = this.editor.getContentHeight();
      const lineHeight = this.editor.getOption(monaco.editor.EditorOption.lineHeight);
      const maxLines = this.getMaxLines() || 30; // Default 30 if not set
      const maxHeight = maxLines * lineHeight;

      const height = Math.min(contentHeight, maxHeight);

      const width = this.elementRef.nativeElement.clientWidth;
      this.elementRef.nativeElement.style.height = `${height}px`;
      this.editor.layout({ width: width, height: height });
  }

  public highlightInitialization(editor: monacoTypes.editor.ICodeEditor): void {
    if(!this.getHighlighting()) {
      return;
    }
    const highlighting = this.getHighlighting().map(inlineHighlight => {
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
      this.cacheCheck = cache;
      this.highlightingCache = highlighting;
      editor.createDecorationsCollection(this.highlightingCache);
    }
  }

  public scrollToLine(editor: monacoTypes.editor.ICodeEditor): void {
    if(!this.getInitialScrollToLine()) {
      return;
    }
    editor.revealLineNearTop(this.getInitialScrollToLine())
  }

  public override setLanguage(val: string) {
    super.setLanguage(val);
    this.updateOptions();
  }

  public override setTheme(val: string) {
    super.setTheme(val);
    this.updateOptions();
  }

  public override setDisabled(val: boolean) {
    super.setDisabled(val);
    this.updateOptions();
  }

  public override setHoverContents(val: HoverContent[]) {
    super.setHoverContents(val)
    if(!val) {
      return;
    }
  }

  public override setHighlighting(val: InlineDecoration[]) {
    super.setHighlighting(val);
    if(this.editor) {
      this.highlightInitialization(this.editor);
    }
  }

  public override setAutoHeight(val: boolean) {
      super.setAutoHeight(val);
      this.updateOptions();
      if (val && this.editor && !this.contentSizeListener) {
          this.updateHeight();
          this.contentSizeListener = this.editor.onDidContentSizeChange(() => {
            this.updateHeight();
        });
      } else if (!val && this.contentSizeListener) {
          this.contentSizeListener.dispose();
          this.contentSizeListener = undefined;
      }
  }

  public updateOptions() : void {
    this._options = {
      value: this._entry,
      language: this._language,
      theme: this._theme,
      readOnly: this._disabled,
      glyphMargin: this._glyphMargin,
      automaticLayout: false,
      scrollBeyondLastLine: !this.isAutoHeight(),
      hover: {
        enabled: true,
        sticky: true,
        above: false
      }
    };
  }
}
