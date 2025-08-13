import enum
import typing
from typing import Any
from montigem_runtime.commandmanager import CommandManager, Command, uncap
from pydantic import BaseModel


class NotificationScope(enum.Enum):
  MINIMAL = 0
  LOCAL = 3
  GLOBAL = 4
  ALL = 5

  def contains(self, other: 'NotificationScope'):
    return other.value <= self.value

GEM_ID = typing.Annotated[int, "is_gem_id"]

class GemBase(BaseModel):
  gemId: int
  TYPE: typing.ClassVar[str] = "ERROR_TYPE"
  PACKAGE: typing.ClassVar[str] = "ERROR_PCK"

  def serialize(self) -> str:
    return self.model_dump_json(exclude_none=True)


def create_gem_deserializer(type: Any):
  return lambda value: deserialize_gem(value, type)

def deserialize_gem(value: Any, type = None) -> 'Gem':
  if isinstance(value, Gem):
    return value
  else:
    return Gem[type](value)


def serialize_gem(value: 'Gem') -> int:
  return value.gemId

def deserialize_gem_list(value: Any) -> 'GemList':
  if isinstance(value, GemList):
    return value
  elif isinstance(value, list):
    r = GemList()
    r.indexes = value
    return r
  elif value is None:
    return GemList()
  else:
    raise 'Unknown type'

def serialize_gem_list(value: 'GemList') -> typing.List[int]:
  return value.indexes


DataT = typing.TypeVar('DataT', bound=GemBase)


class Gem(BaseModel, typing.Generic[DataT]):
  gemId: int = -1

  def __init__(self, /, gemId=-1, **data: Any) -> None:
    super().__init__(**data)
    self.gemId = gemId

  def set_gemid(self, gem_id):
    self.gemId = gem_id


class GemList(BaseModel, typing.Generic[DataT]):
  indexes: typing.List[int] = []

  def add(self, index_in_list: int, gem_id: int):
    self.indexes.insert(index_in_list, gem_id)

  def get_gem_id(self, index_in_list: int):
    return self.indexes[index_in_list]

  def __len__(self):
    return len(self.indexes)

class GlobalGemList(GemList):
  _cache: typing.MutableMapping[int, DataT] = {}

  def is_loaded(self, gem_id: int) -> bool:
    return gem_id in self._cache.keys()

  def set(self, index_in_list: int, obj: DataT):
    assert index_in_list == obj.gemId
    self.indexes.insert(obj.gemId, obj.gemId)
    self._cache[obj.gemId] = obj

  def get(self, gem_id: int) -> DataT:
    return self._cache[gem_id]

class DomainManager:
  global_state: typing.MutableMapping[typing.Type[GemBase], GlobalGemList]
  domain_name: str
  def __init__(self, domain_name: str, command_manager: CommandManager):
    self.domain_name = domain_name
    self.command_manager = command_manager
    self.global_state = {}
  G = typing.TypeVar("G", bound=GemBase)

  def get_global_list(self, domaintype: typing.Type[G]) -> GlobalGemList[G]:
    if domaintype not in self.global_state:
      self.global_state[domaintype] = GlobalGemList()
    return self.global_state[domaintype]

  async def list_filtered(self, domaintype: typing.Type[G]):
    return await self.command_manager.send(
      Command(commandType=f"{domaintype.PACKAGE}.commands.{domaintype.TYPE}_get_filtered"))

  async def get(self, domaintype: typing.Type[G], gem_id: int) -> G:
    # todo: future for multiple calls?
    glist = self.get_global_list(domaintype)
    if glist.is_loaded(gem_id):
      return glist.get(gem_id)

    resp = await self.command_manager.send(
      Command(commandType=f"{domaintype.PACKAGE}.commands.{domaintype.TYPE}_get",
                             gemId=gem_id))
    if not resp['successful']:
      raise "Not successful"
    r = domaintype.model_validate(resp["value"])
    glist.set(gem_id, r)
    return r

  async def get_free_gem_id(self):
    return (await self.command_manager.send(
      Command(commandType=f"{self.domain_name}.commands.GetFreeGemIdRequest")))["value"]

  async def find_all(self, domaintype: typing.Type[G]) -> typing.List[G]:
    r = await self.list_filtered(domaintype)
    if not r["successful"]:
      raise "Not successful"
    results = await asyncio.gather(*(self.get(domaintype, gem_id) for gem_id in r['list']))
    return list(results)

  async def create(self, obj: G) -> G:
    msg = {"commandType": f"{obj.PACKAGE}.commands." + obj.TYPE + "_create", uncap(obj.TYPE): obj}
    r = await self.command_manager.send(
      Command(commandType=f"{obj.PACKAGE}.commands.{obj.TYPE}_create",
                             **{uncap(obj.TYPE): obj}))
    if not r["successful"]:
      raise "Not successful"
    return await self.get(type(obj), obj.gemId)

