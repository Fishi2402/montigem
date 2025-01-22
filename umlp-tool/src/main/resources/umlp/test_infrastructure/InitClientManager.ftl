<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("protected void initClientManager()")}

//use initMe here to avoid re-instantiation of CommandManager
${cdName}Manager.initMe(new ${cdName}ManagerClientImpl());
