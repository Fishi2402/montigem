/* (c) https://github.com/MontiCore/monticore */
import { ProcessModelEditorComponent, config } from 'gui/editprocessmodels/textualview/ProcessModelEditorComponent';

import { ExportProcessModel } from 'demorec/commands/ExportProcessModel';

import { ProcessModel } from 'demorec/ProcessModel'

import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewChild,
  ElementRef,
  OnChanges,
  SimpleChanges,
  OnDestroy
} from '@angular/core';

import { editor } from 'monaco-editor';
import { CommandManager } from "@umlp/common";

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: "ProcessModelEditor.html",
  styleUrls: ["ProcessModelEditor.scss"]
})

export class ProcessModelEditor extends ProcessModelEditorComponent implements OnChanges, OnDestroy {
  @ViewChild('editorSpace', {static: true})
  public container!: ElementRef;

  @Input()
  public language = 'workflow dsl';

  @Input()
  public code?: string;

  @Output()
  public codeChanged = new EventEmitter<string>();

  editor?: editor.IStandaloneCodeEditor;

  public override setProcessModel(val: ProcessModel): void {
    super.setProcessModel(val);
    if(!this.getProcessModel()) {
      return;
    }

    // Obtain the textual representation of the process model and configure the editor
    CommandManager.executeCommand(new ExportProcessModel(this.getProcessModel().getGemId())).then(res => {
      const outcome = res.asSimpleResult();

      if (outcome.isSuccessful()) {
        this.code = outcome.get();

        // Initiate the editor
        const domElement = this.container.nativeElement;
        this.editor = editor.create(domElement, {
          value: this.code,
          language: this.language,
          theme: 'hc-light',
          glyphMargin: true, // Der Rand mit Buttons, etc.
          lightbulb: {
            enabled: editor.ShowLightbulbIconMode.On,
          },
          automaticLayout: true,
          'semanticHighlighting.enabled': true,
        });

        // Emit the current displayed code within the editor
        this.codeChanged.emit(this.code);

        // Decide on what to do if the content in the editor is manipulated
        this.editor.onDidChangeModelContent(() => {
          this.codeChanged.emit(this.editor.getValue());
          this.editor && this.codeChanged.next(this.editor.getValue());
        });
      } else {
        console.error("Failed to serialize the process model in the backend side");
      }
    }).catch(reason => {
      console.error("Failed to serialize the process model in the backend side for the following reason: " + reason);
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
