This experiments demonstrates the capabilities of a python client

* generating a python client using the CDDecGen
  * using a [shared library](runtime-package)
  * [pydantic](https://docs.pydantic.dev/) as a type system
* testing the generated python using [GraalPy](https://www.graalvm.org/python/) and jUnit
  * **Note:** GraalPy is _really_ flaky on windows and requires Java17,
    which is why this project is not run by default



### Domain
## Class
```
class A
```

```python
class A(GemBase):
  TYPE: typing.ClassVar[str] = "A"
  PACKAGE: typing.ClassVar[str] = "mydomain"
```

```python
typeDef: Annotated[
  gembase.Gem[TypeDef], BeforeValidator(gembase.create_gem_deserializer(TypeDef)), PlainSerializer(
    serialize_gem,
    return_type=int)]
```

```python
inputs: Annotated[gembase.GemList, BeforeValidator(
  deserialize_gem_list), PlainSerializer(serialize_gem_list, return_type=typing.List[int])]
```

* `typing.Optional[str] = None`
