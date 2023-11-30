/* (c) https://github.com/MontiCore/monticore */
import { LoginGemModule } from '@src/gui/login/LoginGemModule';
import { ProjectGemModule } from '@src/gui/project/ProjectGemModule';
import { PipelinesGemModule } from '@src/gui/pipelines/PipelinesGemModule';


export const config = {
  declarations:[],
  exports:[],
  imports:[LoginGemModule, ProjectGemModule, PipelinesGemModule],
  providers:[]
}
