/* (c) https://github.com/MontiCore/monticore */

import { Navigation } from 'navigation/navigation.component';
import { MonacoEditorModule } from 'ngx-monaco-editor-v2';

export const config = {
  declarations: [],
  exports: [],
  imports: [Navigation,
    MonacoEditorModule.forRoot({baseUrl: 'assets'})
  ],
  providers: []
}

