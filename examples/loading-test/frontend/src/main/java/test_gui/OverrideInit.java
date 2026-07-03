package test_gui;

import loadingtests.LoadingTestsManager;
import umlp.jsweet.extension.annotation.Component;

@Component
public class OverrideInit extends OverrideInitTOP {

  @Override
  protected void initDomainObj() {
    if (!this._domainObjSetExternally) {
      if (this.test_gui_OverrideInit_domainObj_RoutingId != null) {
        this.setDomainObjInternal(LoadingTestsManager.getPersonSure(this.test_gui_OverrideInit_domainObj_RoutingId));
      }
      else {
        this.setDomainObjInternal(LoadingTestsManager.personBuilder());
      }
    }
    this.initSingleton();
  }

  @Override
  protected void initDomainObjList() {
    if (!this._domainObjListSetExternally) {
      this.setDomainObjListInternal(LoadingTestsManager.getPersonList());
    }
    this.initDomainObj();
  }

  @Override
  protected void initSingleton() {
    if (!this._singletonSetExternally) {
      this.setSingletonInternal(LoadingTestsManager.getUpdateListHelper());
    }
  }
}
