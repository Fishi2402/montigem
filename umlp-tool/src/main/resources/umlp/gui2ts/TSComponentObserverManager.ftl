<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates ComponentObserverManager for the current domain model -->
${tc.signature("AST", "cdName", "cDNames", "domainClassNamesCDMap")}

import { ComponentObserverManager, GemIdentifiable } from "@umlp/commonj2ts";
<#list cDNames as specificCDName>
  import { ${specificCDName}Manager } from "@src/${specificCDName?lower_case}/${specificCDName}Manager";
</#list>

<#list domainClassNamesCDMap as domainClassName, package>
import { ${domainClassName}AllObserver } from "@src/${package?lower_case}/${domainClassName}AllObserver";
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
<#list domainClassNamesCDMap as domainClassName, package>
    this.observerFactory.set("${domainClassName}", (update) => new ${domainClassName}AllObserver(update));
</#list>
    // domain object getter factory
<#list domainClassNamesCDMap as domainClassName, package>
    this.objectGetterFactory.set("${domainClassName}", (gemId) => Promise.resolve(${package}Manager.get${domainClassName}Sure(gemId)));
</#list>
  }

  protected getObserverFactory(): Map<string, ((update: () => void) => any)> {
    return this.observerFactory;
  }

  protected getObjectGetterFactory(): Map<string, ((gemId: number) => Promise<GemIdentifiable>)> {
    return this.objectGetterFactory;
  }
}
