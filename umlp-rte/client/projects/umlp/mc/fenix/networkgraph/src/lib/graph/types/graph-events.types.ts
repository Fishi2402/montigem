import { MouseCaptorEvents } from "sigma/core/captors/mouse";
import { TouchCaptorEvents } from "sigma/core/captors/touch";
import { EventEmitter } from "@angular/core";
import { SigmaEvents } from "sigma/sigma";

/** Defines a generic function type with any number of arguments. */
export type EventType = (...arguments_: any[]) => any;

/** Utility type for converting a tuple to a single parameter, or keeping it as is. */
export type EventParameter<T extends EventType> = T extends EventType
  ? void
  : T extends (...arguments_: [infer P]) => any
    ? P
    : Parameters<T>;

/** Defines a type for lazy events with string keys mapping to EventType. */
export type LazyEventType = { [key: string]: EventType };

/** Type shortcut for mapping SigmaEvents keys to their respective parameter types. */
export type GraphEvent<K extends keyof SigmaEvents> = EventParameter<SigmaEvents[K]>;

/** Type shortcut for creating an EventEmitter with the parameter type of a specific SigmaEvent. */
export type GraphEventEmitter<K extends keyof SigmaEvents> = EventEmitter<GraphEvent<K>>;

/** Type shortcut for mapping MouseCaptorEvents keys to their respective parameter types. */
export type MouseEvent<K extends keyof MouseCaptorEvents> = EventParameter<MouseCaptorEvents[K]>;

/** Type shortcut for creating an EventEmitter with the parameter type of a specific MouseCaptorEvent. */
export type MouseCaptorEventEmitter<K extends keyof MouseCaptorEvents> = EventEmitter<MouseEvent<K>>;

/** Type shortcut for mapping TouchCaptorEvents keys to their respective parameter types. */
export type TouchEvent<K extends keyof TouchCaptorEvents> = EventParameter<TouchCaptorEvents[K]>;

/** Type shortcut for creating an EventEmitter with the parameter type of a specific TouchCaptorEvent. */
export type TouchCaptorEventEmitter<K extends keyof TouchCaptorEvents> = EventEmitter<TouchEvent<K>>;
