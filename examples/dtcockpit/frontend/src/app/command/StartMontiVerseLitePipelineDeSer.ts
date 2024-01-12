/* (c) https://github.com/MontiCore/monticore */

import {CommandDeSer} from '@umlp/common';
import {StartMontiVerseLitePipeline} from "./StartMontiVerseLitePipeline";

export  class StartMontiVerseLitePipelineDeSer extends CommandDeSer<StartMontiVerseLitePipeline>  {

  protected  fromJsonObject (jsonObject: any): StartMontiVerseLitePipeline {
    return new StartMontiVerseLitePipeline();
  }
}


