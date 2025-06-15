import enum
import typing
from typing import Any

from pydantic import BaseModel


class NotificationScope(enum.Enum):
  MINIMAL = 0
  LOCAL = 3
  GLOBAL = 4
  ALL = 5

  def contains(self, other: 'NotificationScope'):
    return other.value <= self.value


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
