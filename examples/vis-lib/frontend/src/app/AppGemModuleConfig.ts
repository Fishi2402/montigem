import { Navigation } from '@src/navigation/navigation.component';
import { Example_basic_Module } from '@src/example_basic/Example_basicGemModule';
import { Person_Module } from '@src/person/PersonGemModule';
import { Example_arrange_Module } from '@src/example_arrange/Example_arrangeGemModule';
import { Example_table_Module } from '@src/example_table/Example_tableGemModule';
import { Gui_Module } from '@src/gui/GuiGemModule';

export const config = {
  declarations: [Navigation],
  exports: [],
  imports: [Person_Module, Gui_Module, Example_arrange_Module,Example_basic_Module,Example_table_Module],
  providers: []
}

