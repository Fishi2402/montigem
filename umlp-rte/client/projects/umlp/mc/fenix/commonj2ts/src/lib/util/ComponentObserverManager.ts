/* (c) https://github.com/MontiCore/monticore */

import {GemIdentifiable} from "../common";
import { Log } from "../logging/Log";

export abstract class ComponentObserverManager {

  private static instance: ComponentObserverManager;

  public static initMe(instance: ComponentObserverManager): void {
    ComponentObserverManager.instance = instance;
  }

  public static getInstance(): ComponentObserverManager {
    if (ComponentObserverManager.instance) {
      return ComponentObserverManager.instance;
    } else {
      Log.error("0xCOM001 ComponentObserverManager not initialized")
      return null;
    }
  }

  // Domain-specific mapping from a type name to a function that create an AllObserver for that type
  protected abstract getObserverFactory(): Map<string, ((update: () => void) => any)>;

  // Domain-specific mapping from a type name to a function that gets an object of that type
  protected abstract getObjectGetterFactory(): Map<string, ((gemId: number) => Promise<GemIdentifiable>)>;

  public static addObserver<T extends GemIdentifiable>(obj: T, update: () => void, updateName: string, component: any): T {
    if (obj === undefined || obj === null) {
      return null;
    }
    return ComponentObserverManager.getInstance().__addObserver(obj, update, updateName, component);
  }

  public static removeObservers(component: any) {
    return ComponentObserverManager.getInstance().__removeObservers(component);
  }

  // GUI Component Name, Domain Object GemId, Method Name, AllObserver
  public componentObservers: Map<string, Map<number, Map<string, any>>> = new Map();
  // Maps gemIds to types
  public objectTypes: Map<number, string> = new Map();

  public __addObserver<T extends GemIdentifiable>(obj: T, update: () => void, updateName: string, componentName: string): T {
    if (!this.componentObservers.has(componentName)) {
      this.componentObservers.set(componentName, new Map<any, Map<string, any>>());
    }
    let updateObservers = this.componentObservers.get(componentName);
    if (!updateObservers.has(obj.getGemId())) {
      updateObservers.set(obj.getGemId(), new Map<string, any>());
      if (!this.objectTypes.has(obj.getGemId())) {
        let protoName = ComponentObserverManager.getPrototypeName(obj);
        this.objectTypes.set(obj.getGemId(), protoName);
      }
    }
    if (!updateObservers.get(obj.getGemId()).has(updateName)) {
      let protoName = ComponentObserverManager.getPrototypeName(obj);
      let observer = this.getObserverFactory().get(protoName)(update);
      updateObservers.get(obj.getGemId()).set(updateName, observer);
      (<any> obj).addObserver(observer)
    }
    return obj;
  }

  public __removeObservers(componentName: string) {
    if (this.componentObservers.has(componentName)) {
      this.componentObservers.get(componentName).forEach((updates, gemId) => {
        updates.forEach((observer, name) => {
          this.getObjectGetterFactory().get(this.objectTypes.get(gemId))(gemId).then((obj: any) => obj.removeObserver(observer));
        });
        updates.clear();
      });
      this.componentObservers.delete(componentName);
    }
  }

  private static getPrototypeName(obj: any) : string {
    if (!obj) {
      return null;
    }
    let name: string = Object.getPrototypeOf(obj).constructor.name;
    if (name.endsWith("Builder")) {
      let superProto = Object.getPrototypeOf(Object.getPrototypeOf(obj));
      if (superProto && name.startsWith(superProto.constructor.name)) {
        return superProto.constructor.name;
      }
    }
    return name;
  }
}
