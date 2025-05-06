/* (c) https://github.com/MontiCore/monticore */
import { config, MonacoEditorComponent } from '../../../../build/gui/mc/fenix/editor/MonacoEditorComponent';
import {EventEmitter, Output, Provider} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {MonacoEditorModule, NGX_MONACO_EDITOR_CONFIG, NgxMonacoEditorConfig } from 'ngx-monaco-editor-v2';
import {editor} from "monaco-editor";

export function provideMonacoEditor(config: NgxMonacoEditorConfig = {}): Provider[] {
  return [
    { provide: NGX_MONACO_EDITOR_CONFIG, useValue: config }
  ];
}

import {
  Component,
} from '@angular/core';


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

export class MonacoEditor extends MonacoEditorComponent  {

  protected cacheCheck : string = "";
  protected highlightingCache;

  /**
   * Adapt the component here if you want to add any options
   */
  public _options : any = {
    value: this._entry,
    language: this._language,
    theme: this._theme,
    readOnly: this._disabled,
    glyphMargin: this._glyphMargin,
    automaticLayout: this._automaticLayout
  };

  @Output() public entryChange = new EventEmitter<string>();

  onEntryChange(event: string) {
    this.entryChange.emit(event);
  }

  public highlightInitialization(editor: editor.ICodeEditor): void {
    console.warn(editor.getModel().getLineCount())
    const highlighting = this.getHighlighting().map(inlineHighlight => {
      console.warn("Highlighting now! ",  inlineHighlight.getCssClass())
      return {
        range: {
          startLineNumber: inlineHighlight.getStartLin(),
          startColumn: inlineHighlight.getStartCol(),
          endLineNumber: inlineHighlight.getEndLin(),
          endColumn: inlineHighlight.getEndCol(),
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
      console.warn("This is called ", highlighting)
      this.cacheCheck = cache;
      this.highlightingCache = highlighting;
      editor.createDecorationsCollection(this.highlightingCache);
    }
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

  public updateOptions() : void {
    this._options = {
      value: this._entry,
      language: this._language,
      theme: this._theme,
      readOnly: this._disabled,
      glyphMargin: this._glyphMargin,
      automaticLayout: this._automaticLayout
    };
  }
}
