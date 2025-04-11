import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppGemModule } from 'AppGemModule';

platformBrowserDynamic().bootstrapModule(AppGemModule)
  .catch(err => console.error(err));