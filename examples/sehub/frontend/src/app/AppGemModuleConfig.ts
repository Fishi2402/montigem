import { Navigation } from '@src/navigation/navigation.component';
import { Login_Module } from '@src/gui/login/LoginGemModule';
import { Issues_Module } from '@src/gui/issues/IssuesGemModule';
import { Usersettings_Module } from '@src/gui/usersettings/UsersettingsGemModule';

export const config = {
  declarations: [Navigation],
  exports: [],
  imports: [Login_Module, Issues_Module, Usersettings_Module],
  providers: []
}
