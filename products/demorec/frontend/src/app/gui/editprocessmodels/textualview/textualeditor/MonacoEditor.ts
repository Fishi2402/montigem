/* (c) https://github.com/MontiCore/monticore */
import { MonacoEditorComponent, config } from 'gui/editprocessmodels/textualview/textualeditor/MonacoEditorComponent';

import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewChild,
  ElementRef,
  OnChanges,
  SimpleChanges,
  OnDestroy,
  AfterViewInit
} from '@angular/core';

import { editor } from 'monaco-editor';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: "MonacoEditor.html",
  styleUrls: ["MonacoEditor.scss"]
})

export class MonacoEditor extends MonacoEditorComponent implements AfterViewInit, OnChanges, OnDestroy {
  @ViewChild('editorSpace', {static: true})
  public container!: ElementRef;

  @Input()
  public code?: string;

  @Output()
  public codeChanged = new EventEmitter<string>();

  editor?: editor.IStandaloneCodeEditor;

  public ngAfterViewInit(): void {
      console.error("Opened the monaco editor")
      // Initiate the editor
      const domElement = this.container.nativeElement;
      this.editor = editor.create(domElement, {
        /* Find more options: https://microsoft.github.io/monaco-editor/typedoc/interfaces/editor.IStandaloneEditorConstructionOptions.html */
        value: this.code,
        language: this.getLanguage(),
        theme: this.getTheme(), // e.g. 'vs-dark'
        glyphMargin: this.isGlyphMargin(), // Margin for buttons, lightbulb, collapse icons, etc.
        lightbulb: {
          enabled: this.isShowLightbulbIconMode() ? editor.ShowLightbulbIconMode.On : editor.ShowLightbulbIconMode.Off,
        },
        automaticLayout: this.isAutomaticLayout(),
        'semanticHighlighting.enabled': this.isSemanticHighlightingEnabled(),
      });

      // Emit the current displayed code within the editor
      this.codeChanged.emit(this.code);

      // Decide on what to do if the content in the editor is manipulated
      this.editor.onDidChangeModelContent(() => {
        this.codeChanged.emit(this.editor.getValue());
        this.editor && this.codeChanged.next(this.editor.getValue());
      });
  }

  public ngOnChanges(changes: SimpleChanges): void {
    if (changes['code']) {
      this.editor?.setValue(changes['code'].currentValue)
    }
  }

  public override ngOnDestroy(): void {
    super.ngOnDestroy();
    this.editor?.dispose();
  }
}
