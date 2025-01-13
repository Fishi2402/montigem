import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppGemModule } from '@src/AppGemModule';


platformBrowserDynamic().bootstrapModule(AppGemModule)
  .catch(err => console.error(err));
