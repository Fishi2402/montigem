import sys
sys.path.append('build/py_out')
sys.path.append('runtime-package/src')

from MyDomain import A

from montigem_runtime.gembase import DomainManager
from montigem_runtime.commandmanager import CommandManager

class MyDomainManager(DomainManager):
  def __init__(self, command_manager: CommandManager):
    super().__init__("MyDomain", command_manager)

command_manager: CommandManager = CommandManager()
my_domain_manager = MyDomainManager(command_manager)


import asyncio

async def inputhandler():
  await asyncio.sleep(1)
  print("sleep done")

async def main():
  async def external_consumer():
    """Simulates an external event loop consuming messages from the incoming queue."""
    while True:
      message = await command_manager.queue_in.get()  # Wait for a message
      print(f"Received message: {message}")
      if "commandType" in message:
        if message["commandType"] == "wbgeo.commands.Runner_addJob":
          a = await my_domain_manager.get(A, message["gemId"])
          # await runner.jobs_add(message["indexInList"], message["jobGemId"],
          #                       NotificationScope.LOCAL)
          print("DONE")
        print(message["commandType"])
      else:
        print("TODO")

  consumer = asyncio.create_task(external_consumer())
  inputkler = asyncio.create_task(inputhandler())

  # await asyncio.gather(client, consumer, inputkler)
  # loop = asyncio.new_event_loop()
  # loop.run_until_complete(start_client())
  commtasks = asyncio.create_task(command_manager.connect())

  await asyncio.gather(commtasks, consumer, inputkler)
if __name__ == "__main__":
  asyncio.run(main())
