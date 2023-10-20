import { Navigation } from '@src/navigation/navigation.component';

import { Person_Module} from '@src/person/PersonGemModule';

import { Gui_Module} from '@src/gui/GuiGemModule';
export const config={
  declarations:[Navigation],
  exports:[],
  imports:[Person_Module, Gui_Module],
  providers:[]
}

