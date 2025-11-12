import sys
sys.path.append('build/py_out')
sys.path.append('runtime-package/src')

import asyncio
from montigem_runtime.gembase import DomainManager
from montigem_runtime.commandmanager import CommandManager, ainput


from MyDomain import A
from MyDomain import B


class MyDomainManager(DomainManager):
  def __init__(self, command_manager: CommandManager):
    super().__init__("mydomain", command_manager)

command_manager: CommandManager = CommandManager()
my_domain_manager = MyDomainManager(command_manager)


async def start():
  async def external_consumer():
    import MyDomain.MyDomainCommandHandler
    cmd_handler = MyDomain.MyDomainCommandHandler.MyDomainCommandHandler()
    """Simulates an external event loop consuming messages from the incoming queue."""
    while True:
      message = await command_manager.queue_in.get()  # Wait for a message
      print(f"Received message: {message}")
      if "commandType" in message:
        await cmd_handler.handleCommand(message, my_domain_manager)
      else:
        print("TODO")

  consumer = asyncio.create_task(external_consumer())
  commtasks = asyncio.create_task(command_manager.connect())

  await asyncio.gather(commtasks, consumer)
