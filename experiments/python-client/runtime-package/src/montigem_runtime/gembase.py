import enum
import typing
from typing import Any
from montigem_runtime.commandmanager import CommandManager, Command, uncap
from pydantic import BaseModel
from collections.abc import Iterator, AsyncIterator
import asyncio

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

def create_gem_list_deserializer(type: Any):
  return lambda value: deserialize_gem_list(value, type)


def deserialize_gem_list(value: Any, type=None) -> 'GemList':
  if isinstance(value, GemList):
    return value
  elif isinstance(value, list): #todo: is origin?
    r = GemList[type]()
    r.indexes = value
    return r
  elif value is None:
    return GemList[type]()
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

  def get_gem_id(self, index_in_list: int) -> GEM_ID:
    return self.indexes[index_in_list]

  def get_index_of_gem_id(self, gem_id: GEM_ID) -> int:
    return self.indexes.index(gem_id)

  def pop(self, index_in_list: int) -> GEM_ID:
    return self.indexes.pop(index_in_list)

  def __len__(self) -> int:
    return len(self.indexes)

  def gem_ids(self) -> Iterator[GEM_ID]:
    return map(self.get_gem_id, self.indexes)

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

class GemProtocolException(Exception):
  def __init__(self, message, command: typing.Optional[Command], resp:Any):
    self.message = message
    super().__init__(self.message)

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

  def is_loaded_locally(self, domaintype: typing.Type[G], gem_id: GEM_ID) -> bool:
    return self.get_global_list(domaintype).is_loaded(gem_id)

  async def get(self, domaintype: typing.Type[G], gem_id: int) -> G:
    # todo: future for multiple calls?
    glist = self.get_global_list(domaintype)
    if glist.is_loaded(gem_id):
      return glist.get(gem_id)

    command = Command(commandType=f"{domaintype.PACKAGE}.commands.{domaintype.TYPE}_get",
            gemId=gem_id)
    resp = await self.command_manager.send(command)
    if not resp['successful']:
      raise GemProtocolException(f"Get of {domaintype}#{gem_id} not successful", command=command, resp=resp)
    r = domaintype.model_validate(resp["value"])
    glist.set(gem_id, r)
    return r

  async def get_free_gem_id(self):
    return (await self.command_manager.send(
      Command(commandType=f"{self.domain_name}.commands.GetFreeGemIdRequest")))["value"]

  async def find_all(self, domaintype: typing.Type[G]) -> typing.List[G]:
    r = await self.list_filtered(domaintype)
    if not r["successful"]:
      raise GemProtocolException(f"Find all of {domaintype} not successful", command=None, resp=r)
    results = await asyncio.gather(*(self.get(domaintype, gem_id) for gem_id in r['list']))
    return list(results)

  async def create(self, obj: G) -> G:
    cmd = Command(commandType=f"{obj.PACKAGE}.commands.{obj.TYPE}_create",
           **{uncap(obj.TYPE): obj})
    r = await self.command_manager.send(cmd)
    if not r["successful"]:
      raise GemProtocolException(f"Create not successful", command=cmd, resp=r)
    return await self.get(type(obj), obj.gemId)


  async def get_list(self, domaintype: typing.Type[G], gem_id_iterator:Iterator[GEM_ID]) -> AsyncIterator[G]:
    """

    [obj.name async for obj in my_manager.get_list(MyDomain.Type, parent.list.gem_ids()]

    :param domaintype: domain class, similar to #get
    :param gem_id_iterator: the iterator
    :return:
    """
    for gem_id in gem_id_iterator:
      yield await self.get(domaintype, gem_id)
