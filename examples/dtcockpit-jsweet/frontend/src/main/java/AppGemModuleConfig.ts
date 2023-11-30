import { Navigation } from '@src/navigation/navigation.component';
import { LoginGemModule } from '@src/gui/login/LoginGemModule';
import { ProjectGemModule } from '@src/gui/project/ProjectGemModule';
import { PipelinesGemModule } from '@src/gui/pipelines/PipelinesGemModule';


export const config = {
  declarations:[Navigation],
  exports:[],
  imports:[LoginGemModule, ProjectGemModule, PipelinesGemModule],
  providers:[]
}
