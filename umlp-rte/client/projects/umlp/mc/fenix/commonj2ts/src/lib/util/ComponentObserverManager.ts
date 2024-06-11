/* (c) https://github.com/MontiCore/monticore */

import {GemIdentifiable} from "../common";

export abstract class ComponentObserverManager {

  private static instance: ComponentObserverManager;

  public static initMe(instance: ComponentObserverManager): void {
    ComponentObserverManager.instance = instance;
  }

  protected abstract getObserverFactory(): Map<string, any>;

  protected abstract getObjectGetterFactory(): Map<string, any>;

  public static addObserver<T extends GemIdentifiable>(obj: T, update: () => void, updateName: string, component: any): T {
    if (obj === undefined || obj === null) {
      return null;
    }
    return ComponentObserverManager.instance.__addObserver(obj, update, updateName, component);
  }

  public static removeObservers(component: any) {
    return ComponentObserverManager.instance.__removeObservers(component);
  }

  // GUI Component Name, Domain Object GemId, Method Name, AllObserver
  public componentObservers: Map<string, Map<number, Map<string, any>>> = new Map();
  public objectTypes: Map<number, string> = new Map();

  public __addObserver<T extends GemIdentifiable>(obj: T, update: () => void, updateName: string, componentName: string): T {
    if (!this.componentObservers.has(componentName)) {
      this.componentObservers.set(componentName, new Map<any, Map<string, any>>());
    }
    let updateObservers = this.componentObservers.get(componentName);
    if (!updateObservers.has(obj.getGemId())) {
      updateObservers.set(obj.getGemId(), new Map<string, any>());
    }
    if (!updateObservers.get(obj.getGemId()).has(updateName)) {
      let protoName = ComponentObserverManager.getPrototypeName(obj);
      let observer = this.getObserverFactory().get(protoName)(update);
      updateObservers.get(obj.getGemId()).set(updateName, observer);
      (<any> obj).addObserver(observer)

      if (!this.objectTypes.has(obj.getGemId())) {
        this.objectTypes.set(obj.getGemId(), protoName);
      }
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
