import {ExampleComponent, config} from "example_monaco/ExampleComponent";
import {Component} from "@angular/core";
import { MonacoEditorModule  } from 'ngx-monaco-editor-v2';
import { FormsModule } from '@angular/forms';

/**
 * Example for a monaco editor pane
 */
@Component({
  standalone: true,
  imports: [...config.imports, MonacoEditorModule, FormsModule],
  selector: config.selector,
  templateUrl: 'Example.html',
  styles: config.styles,
  styleUrls: config.styleUrls
})
export class Example extends ExampleComponent {
  editorOptions = {theme: 'vs', language: 'javascript'};
  code: string= 'function x() {\nconsole.log("Hello world!");\n}';


}
