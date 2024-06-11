<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates ComponentObserverManager for the current domain model -->
${tc.signature("AST", "cdName", "listDomainClassNames", "packageName")}

import { ComponentObserverManager, GemIdentifiable } from "@umlp/commonj2ts";
import { ${cdName}Manager } from "@src/${packageName}/${cdName}Manager";
<#list listDomainClassNames as domainClassName>
import { ${domainClassName}AllObserver } from "@src/${packageName}/${domainClassName}AllObserver";
</#list>

export class ${cdName}ComponentObserverManager extends ComponentObserverManager {

  public static init(): void {
    ComponentObserverManager.initMe(new ${cdName}ComponentObserverManager());
  }

  // Domain-specific mapping from a type name to a function that create an AllObserver for that type
  private observerFactory: Map<string, ((update: () => void) => any)> = new Map;

  // Domain-specific mapping from a type name to a function that gets an object of that type
  private objectGetterFactory: Map<string, ((gemId: number) => Promise<GemIdentifiable>)> = new Map();

  constructor() {
    super();
    // AllObserver factory
<#list listDomainClassNames as domainClassName>
    this.observerFactory.set("${domainClassName}", (update) => new ${domainClassName}AllObserver(update));
</#list>
    // domain object getter factory
<#list listDomainClassNames as domainClassName>
    this.objectGetterFactory.set("${domainClassName}", (gemId) => FunctionStructure4FenixManager.get${domainClassName}Sure(gemId));
</#list>
  }

  protected getObserverFactory(): Map<string, ((update: () => void) => any)> {
    return this.observerFactory;
  }

  protected getObjectGetterFactory(): Map<string, ((gemId: number) => Promise<GemIdentifiable>)> {
    return this.objectGetterFactory;
  }
}
