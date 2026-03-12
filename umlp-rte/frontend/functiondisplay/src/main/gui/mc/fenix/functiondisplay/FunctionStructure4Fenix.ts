/* (c) https://github.com/MontiCore/monticore */

import { Function4Display } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Function4Display";
import { Port4Display } from "../../../../../../build/gui2java2ts/mc/fenix/functiondisplay/functionstructure4fenix/Port4Display";

/**
 * Intermediate representation of a function for ELK layouting.
 */
export interface Funktion4Elk {
  /** Label of the node. */
  name: string;
  /** Reference to the original CD model. */
  self: Function4Display;
  /** List of ports associated with this node. */
  ports: Port4Elk[];
  /** List of directed connections between ports. */
  connections: [Port4Elk, Port4Elk][];
  /** Sub-functions (children) for hierarchical layouting. */
  parts: Funktion4Elk[];
}

/**
 * Intermediate representation of a port for ELK layouting.
 */
export interface Port4Elk {
  /** True if it's an input port, false for output. */
  isIn: boolean;
  /** Reference to the containing function node. */
  funktion: Funktion4Elk;
  /** Unique ID for ELK (usually 'Function.Port'). */
  id: string;
  /** Reference to the original CD port model. */
  self: Port4Display;
}
