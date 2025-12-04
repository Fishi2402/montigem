This experiments demonstrates the capabilities of a python client

* generating a python client using the CDDecGen
  * using a [shared library](runtime-package)
  * [pydantic](https://docs.pydantic.dev/) as a type system
* testing the generated python using [GraalPy](https://www.graalvm.org/python/) and jUnit
  * **Note:** GraalPy is _really_ flaky on windows and requires Java17,
    which is why this project is not run by default

In case your IDE is continuously re-indexing: Congrats on finding `IDEA-381693`: Update to 2025.3 RC or newer.

Feature Set:
  * [X] domain classes
  * [X] domain attributes
  * [ ] Type-System (WIP)
    * right now one has to always pass the GEM_ID
    * quite a few manager calls required for now
    * [ ] TODO: use typing.overridden
  * Assocs:
    * [X] basic support (`list.get_gem_id()` - requires manual manager calling to retrieve objects)
    * [X] of domain objects
      * [ ] bidirectional (associations package)
    * [X] of built-tin objects (String, Integer)
    * Cap:
      * [X] mandatory
      * [X] optional
        * [ ] setAbsent currently requires set calls with `-1`
      * [X] Mult
        * Does not differentiate between ordered/unordered (same as MontiGem)
  * [ ] delete client
  * [ ] method-commands
    * currently requires manual calling
    ````python
    await CommandManager.instance.send(
      Command(commandType="wbgeo.commands.CachedValue_setTrace",
        gemId=o_0_val.gemId,
        traceGemId=cachedExec.gemId))
    ````


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


### Note on Python Generation:

#### imports

In python, files represent modules and not classes.

This results in the module `MyDomain.MyClass` defining
the class `MyDomain.MyClass.MyClass`.

We apologize for the cursed imports you will have to deal with!

#### expressions

The Expressions shipped with CD4A do not follow python syntax.
For generic types, we have overriden the `PythonMYSimpleGenericTypesPrinter` pretty printer.

For other initial values, we just abuse the fact that a NameExpression can hold any String:
E.g., myList = `[1,2,3]` is an ArrayIndexAccessExpr with the Name `1,2,3` as the index-expression.

### Running locally

> cd umlp\experiments\python-client
> $env:PYTHONPATH="backend/build/py_out;runtime-package/src"

### How to use:

Install `montigem_runtime` via pip

```python
from montigem_runtime.commandmanager import CommandManager

# initialize
command_manager: CommandManager = CommandManager()
my_domain_manager = MyDomainManager.MyDomainManager(command_manager)

#
cmd_handler = MyDomain.MyDomainCommandHandler.MyDomainCommandHandler()

async def main():
  async def external_consumer():
    while True:
      message = await command_manager.queue_in.get()  # Wait for a message
      print(f"Received message: {message}", file=sys.stderr)
      if "commandType" in message:
        await cmd_handler.handleCommand(message, my_domain_manager)
      else:
        raise Exception("Unexpected message, missing commandType")

  consumer = asyncio.create_task(external_consumer())

  host = "localhost"
  port = "8081"
  commtasks = asyncio.create_task(command_manager.connect(f"ws://{host}:{port}/umlp/api/command"))

  await asyncio.gather(commtasks, consumer)
if __name__ == "__main__":
  asyncio.run(main())
```
