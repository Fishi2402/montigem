/* (c) https://github.com/MontiCore/monticore */
import {CommandManager} from '../command/CommandManager';

export interface TestContext {
  activate();

  getCommandManager(): CommandManager;
}


