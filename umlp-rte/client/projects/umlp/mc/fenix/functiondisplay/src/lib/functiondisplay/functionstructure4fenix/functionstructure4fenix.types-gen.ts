import {Function4Display} from "..";
import {Port4Display} from "..";

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
