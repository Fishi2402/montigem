/* (c) https://github.com/MontiCore/monticore */
import { Login_Module } from '@src/gui/login/LoginGemModule';
import { Project_Module } from '@src/gui/project/ProjectGemModule';
import { Pipelines_Module } from '@src/gui/pipelines/PipelinesGemModule';


export const config = {
  declarations:[],
  exports:[],
  imports:[Login_Module, Project_Module, Pipelines_Module],
  providers:[]
}
