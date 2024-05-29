export abstract class ComponentObserverManager {

  private static instance: ComponentObserverManager;

  public static initMe(instance: ComponentObserverManager): void {
    ComponentObserverManager.instance = instance;
  }

  protected abstract getObserverFactory(): Map<string, any>;

                     // component, DomainObject, MethodenName, Observer
  public componentObservers: Map<any, Map<any, Map<string, any>>> = new Map();

  public static addObserver<T>(obj: T, update: () => void, updateName: string, component: any): T {
    if (obj === undefined || obj === null) {
      return null;
    }
    return ComponentObserverManager.instance.__addObserver(obj, update, updateName, component);
  }

  public static removeObservers(component: any) {
    return ComponentObserverManager.instance.__removeObservers(component);
  }

  public __addObserver<T>(obj: T, update: () => void, updateName: string, component: any): T {
    if (!this.componentObservers.has(component)) {
      this.componentObservers.set(component, new Map<any, Map<string, any>>());
    }
    let updateObservers = this.componentObservers.get(component);
    if (!updateObservers.has(obj)) {
      updateObservers.set(obj, new Map<string, any>());
    }
    if (!updateObservers.get(obj).has(updateName)) {
      let protoName = ComponentObserverManager.getPrototypeName(obj);
      let observer = this.getObserverFactory().get(protoName)(update);
      updateObservers.get(obj).set(updateName, observer);
      (<any> obj).addObserver(observer)
    }
    return obj;
  }

  public __removeObservers(component: any) {
    if (this.componentObservers.has(component)) {
      this.componentObservers.get(component).forEach((update, obj) => {
        update.forEach((observer, name) => {
          obj.removeObserver(observer);
        });
        update.clear();
      });
      this.componentObservers.delete(component);
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
