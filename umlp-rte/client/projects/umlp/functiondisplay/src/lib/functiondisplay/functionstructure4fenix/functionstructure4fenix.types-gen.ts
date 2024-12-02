import {Function4Display} from "../../gen/functionstructure4fenix/Function4Display";
import {Port4Display} from "../../gen/functionstructure4fenix/Port4Display";

export interface Funktion4Elk {
  name: string;
  self: Function4Display;
  ports: Port4Elk[];
  connections: [Port4Elk, Port4Elk][];
  parts: Funktion4Elk[];
}
export interface Port4Elk {
  isIn: boolean;
  funktion : Funktion4Elk;
  id : string;
  self:Port4Display;
}
