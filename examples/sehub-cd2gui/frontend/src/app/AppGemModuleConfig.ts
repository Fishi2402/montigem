/* (c) https://github.com/MontiCore/monticore */
import { Navigation } from './navigation/navigation.component';


import {ModelsGemModule} from '@src/cd2gui/models/ModelsGemModule';

export const config={
  declarations:[Navigation],
  exports:[],
  imports:[ModelsGemModule],
  providers:[]
}
