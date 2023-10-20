import { Navigation } from '@src/navigation/navigation.component';
import { Login_Module } from '@src/gui/login/LoginGemModule';
import { Project_Module } from '@src/gui/project/ProjectGemModule';

export const config = {
  declarations:[Navigation],
  exports:[],
  imports:[Login_Module, Project_Module],
  providers:[]
}
