import { ComponentObserverManager, GemIdentifiable } from "@umlp/commonj2ts";

export class DummyComponentObserverManager extends ComponentObserverManager {

  public static init(): void {
    ComponentObserverManager.initMe(new DummyComponentObserverManager());
  }

  // Domain-specific mapping from a type name to a function that create an AllObserver for that type
  private observerFactory: Map<string, ((update: () => void) => any)> = new Map;

  // Domain-specific mapping from a type name to a function that gets an object of that type
  private objectGetterFactory: Map<string, ((gemId: number) => Promise<GemIdentifiable>)> = new Map();

  constructor() {
    super();
  }

  protected getObserverFactory(): Map<string, ((update: () => void) => any)> {
    return this.observerFactory;
  }

  protected getObjectGetterFactory(): Map<string, ((gemId: number) => Promise<GemIdentifiable>)> {
    return this.objectGetterFactory;
  }
}
