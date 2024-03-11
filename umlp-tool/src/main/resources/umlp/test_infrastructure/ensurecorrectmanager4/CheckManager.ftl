<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("protected void checkManager()")}

${cdName}Manager inst = ${cdName}Manager.getSingleton();
if (inst != manager) {
  Log.error("Mutator called on object, but the manager changed. " +
  "Please activate the correct context before calling the mutator. " +
  "Manager responsible for this object: " + manager + ", current manager: " + inst);
}
