<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates ComponentObserverManager for the current domain model -->
${tc.signature("AST", "cdName", "listDomainClassNames", "packageName")}

import { ComponentObserverManager } from "@umlp/commonj2ts";
<#list listDomainClassNames as domainClassName>
import { ${domainClassName}AllObserver } from "@src/${packageName}/${domainClassName}AllObserver";
</#list>

export class ${cdName}ComponentObserverManager extends ComponentObserverManager {
  public static init() : void {
    ComponentObserverManager.initMe(new ${cdName}ComponentObserverManager());
  }

  constructor() {
    super();
<#list listDomainClassNames as domainClassName>
  this.observerFactory.set("${domainClassName}", (update) => new ${domainClassName}AllObserver(update));
</#list>

  }

  private observerFactory: Map<string, any> = new Map;
    protected getObserverFactory(): Map<string, any> {
    return this.observerFactory;
  }
}
