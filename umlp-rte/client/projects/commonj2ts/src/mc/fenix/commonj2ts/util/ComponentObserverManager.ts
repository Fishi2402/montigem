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

  public static addDomainObjectObserver<T extends GemIdentifiable>(obj: T, type: string, update: () => void, updateName: string, componentName: string): T {
    if (obj === undefined || obj === null) {
      return null;
    }
    return ComponentObserverManager.getInstance().__addDomainObjectObserver(obj, type, update, updateName, componentName);
  }

  public static addStateVariableObserver<T>(obj: T, variableName: string, update: () => void, updateName: string, componentName: string): T {
    return ComponentObserverManager.getInstance().__addStateVariableObserver(obj, variableName, update, updateName, componentName);
  }

  public static callStateVariableObserver(variableName: string, componentName: string): boolean {
    return ComponentObserverManager.getInstance().__callStateVariableObserver(variableName, componentName);
  }

  public static removeObservers(componentName: string) {
    return ComponentObserverManager.getInstance().__removeObservers(componentName);
  }

  // GUI Component Name, Domain Object GemId, Method Name, AllObserver
  public componentDomainObjectObservers: Map<string, Map<number, Map<string, any>>> = new Map();
  // Maps gemIds to types
  public objectTypes: Map<number, string> = new Map();

  public __addDomainObjectObserver<T extends GemIdentifiable>(obj: T, type: string, update: () => void, updateName: string, componentName: string): T {
    if (!this.componentDomainObjectObservers.has(componentName)) {
      this.componentDomainObjectObservers.set(componentName, new Map<number, Map<string, any>>());
    }
    let updateObservers: Map<number, Map<string, any>> = this.componentDomainObjectObservers.get(componentName);
    if (!updateObservers.has(obj.getGemId())) {
      updateObservers.set(obj.getGemId(), new Map<string, any>());
      if (!this.objectTypes.has(obj.getGemId())) {
        this.objectTypes.set(obj.getGemId(), type);
      }
    }
    if (!updateObservers.get(obj.getGemId()).has(updateName)) {
      let observer = this.getObserverFactory().get(type)(update);
      updateObservers.get(obj.getGemId()).set(updateName, observer);
      (<any> obj).addObserver(observer)
    }
    return obj;
  }

  // GUI Component Name, Variable Name, Method Name, Method
  public componentStateVariableObservers: Map<string, Map<string, Map<string, () => void>>> = new Map();
  public __addStateVariableObserver<T>(obj: T, variableName: string, update: () => void, updateName: string, componentName: string): T {
    if (!this.componentStateVariableObservers.has(componentName)) {
      this.componentStateVariableObservers.set(componentName, new Map<string, Map<string, () => void>>());
    }
    let updateObservers: Map<string, Map<string, () => void>> = this.componentStateVariableObservers.get(componentName);
    if (!updateObservers.has(variableName)) {
      updateObservers.set(variableName, new Map<string, () => void>());
    }
    if (!updateObservers.get(variableName).has(updateName)) {
      updateObservers.get(variableName).set(updateName, update);
    }
    return obj;
  }

  private __callStateVariableObserver(variableName: string, componentName: string): boolean {
    if (!this.componentStateVariableObservers.has(componentName)) {
      return false;
    }
    if (!this.componentStateVariableObservers.get(componentName).has(variableName)) {
      return false;
    }
    this.componentStateVariableObservers.get(componentName).get(variableName)
      .forEach((fkn: () => void) => {
        fkn();
    })
    return true;
  }

  public __removeObservers(componentName: string) {
    if (this.componentDomainObjectObservers.has(componentName)) {
      this.componentDomainObjectObservers.get(componentName).forEach((updates, gemId) => {
        updates.forEach((observer, name) => {
          this.getObjectGetterFactory().get(this.objectTypes.get(gemId))(gemId).then((obj: any) => obj.removeObserver(observer));
        });
        updates.clear();
      });
      this.componentDomainObjectObservers.delete(componentName);
    }

    if (this.componentStateVariableObservers.has(componentName)) {
      this.componentStateVariableObservers.get(componentName).forEach((updates, variableName) => {
        updates.clear();
      });
      this.componentStateVariableObservers.delete(componentName);
    }
  }

  private static getPrototypeName(obj: any) : string {
    if (!obj) {
      return null;
    }
    let name: string = Object.getPrototypeOf(obj).constructor.name;
    console.log("Constructor Name: " + name);
    if (name.endsWith("Builder")) {
      let superProto = Object.getPrototypeOf(Object.getPrototypeOf(obj));
      if (superProto && name.startsWith(superProto.constructor.name)) {
        console.log("Super Constructor Name: " + name);
        return superProto.constructor.name;
      }
    }
    return name;
  }
}
