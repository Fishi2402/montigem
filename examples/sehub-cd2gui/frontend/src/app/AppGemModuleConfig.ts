import { Navigation } from './navigation/navigation.component';

import {Login_Module} from '@src/gui/login/LoginGemModule';

import {Issues_Module} from '@src/gui/issues/IssuesGemModule';

import {Usersettings_Module} from '@src/gui/usersettings/UsersettingsGemModule';

import {Models_Module} from '../../build/umlp/fe2/cd2gui/models/ModelsGemModule'
export const config={
  declarations:[Navigation],
  exports:[],
  imports:[Login_Module, Issues_Module,Usersettings_Module,Models_Module],
  providers:[]
}
