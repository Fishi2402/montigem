/* (c) https://github.com/MontiCore/monticore */

import {Command} from "./Command";

export type CommandReceiveObserver = (arg: Command<any>) => void;
