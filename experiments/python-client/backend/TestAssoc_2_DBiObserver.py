# (c) https://github.com/MontiCore/monticore */


# from __future__ import annotations
import typing
import pydantic
import montigem_runtime

# printImportList
import MyDomain.TestAssoc_2_DObserver;
import MyDomain.MyDomainManager;


# end printImportList

from montigem_runtime.gembase import DomainManager


class TestAssoc_2_DBiObserver(MyDomain.TestAssoc_2_DObserver.TestAssoc_2_DObserver):

  def getScope(self, ) -> montigem_runtime.gembase.NotificationScope:
    return montigem_runtime.gembase.NotificationScope.LOCAL

  async def notifySetoptA(self, testAssoc_2_D: typing.Any, _old_value: typing.Any,
                          optA: typing.Any):
    if MyDomain.MyDomainManager.MyDomainManager.INSTANCE.is_loaded(TestAssoc_2_D, optA):
      optA = await MyDomain.MyDomainManager.MyDomainManager.INSTANCE.get(TestAssoc_2_D, optA)
      optA.setOpt(testAssoc_2_D.gemId, montigem_runtime.gembase.NotificationScope.MINIMAL)
    if MyDomain.MyDomainManager.MyDomainManager.INSTANCE.is_loaded(TestAssoc_2_D, _old_value):
      # Clear old other side
      optA = await MyDomain.MyDomainManager.MyDomainManager.INSTANCE.get(TestAssoc_2_D, _old_value)
      optA.setOpt(-1, montigem_runtime.gembase.NotificationScope.MINIMAL)


TestAssoc_2_DBiObserver.INSTANCE = TestAssoc_2_DBiObserver()
