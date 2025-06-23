/* (c) https://github.com/MontiCore/monticore */
import {CommandManager, Event} from '@umlp/common';

import { TextualViewComponent, config } from 'gui/editprocessmodels/textualview/TextualViewComponent';

import { TagProcessModel } from 'demorec/commands/TagProcessModel';

import { GenerateInstructions } from 'demorec/commands/GenerateInstructions';

import {
  Component,
  OnInit,
} from '@angular/core';

@Component({
  standalone:true,
  imports:[...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: config.styleUrls
})

export class TextualView extends TextualViewComponent implements OnInit {
  public currentEditorCode: string;

  public override ngOnInit(): void {
    super.ngOnInit();

    requestAnimationFrame(() => {
      document.querySelector('.graphicalViewButton > button.gem-button').classList.add('clicked');
      document.querySelector('.textualViewButton > button.gem-button').classList.remove('clicked');
    });
  }

  public override getEditorsCurrentCode(): Event<string> {
    return (currentCode) => {
      this.currentEditorCode = currentCode;
    }
  }

  public override downloadModel(): Event<void> {
    return $ => {
      const data = this.currentEditorCode;
      const blob = new Blob([data], { type: 'text/plain' });
      const link = document.createElement('a');

      link.href = window.URL.createObjectURL(blob);
      if (this.getPm().getUnderlyingProcessGem() !== null) {
        link.download = "process_model_of_process_with_gemid_" + this.getPm().getUnderlyingProcess().getGemId() + ".wfm";
      }
      else {
        link.download = "process_model_of_anonymous_process_" + Date.now() + ".wfm";
      }
      link.click();
      window.URL.revokeObjectURL(link.href);
    }
  }

  public override downloadTags (): Event<void> {
    return $ => {
      CommandManager.executeCommand(new TagProcessModel(this.getPm().getGemId(), '')).then(res => {
        const outcome = res.asSimpleResult();
        if (outcome.isSuccessful()) {
          const data = outcome.get();
          const blob = new Blob([data], { type: 'text/plain' });
          const link = document.createElement('a');
          link.href = window.URL.createObjectURL(blob);
          link.download = "tags_of_process_model_with_gemid_" + this.getPm().getGemId() + ".tags";
          link.click();
          window.URL.revokeObjectURL(link.href);
        }
      });
    }
  }

  public override downloadInstructions (): Event<void> {
    return $ => {
      CommandManager.executeCommand(new GenerateInstructions(this.getPm().getGemId(), '', '')).then(res => {
        const outcome = res.asSimpleResult();
        if (outcome.isSuccessful()) {
          const data = outcome.get();
          const blob = new Blob([data], { type: 'text/plain' });
          const link = document.createElement('a');
          link.href = window.URL.createObjectURL(blob);
          link.download = "instructions_for_process_with_gemid_" + this.getPm().getUnderlyingProcess().getGemId() + ".txt";
          link.click();
          window.URL.revokeObjectURL(link.href);
        }
      });
    }
  }
}
