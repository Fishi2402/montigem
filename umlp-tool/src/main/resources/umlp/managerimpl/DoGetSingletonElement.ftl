<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name", "managerName")}
${cd4c.method("protected ${name} _doGet${name}()")}
if (${name?uncap_first}Instance == null) { // Fetch from DAO
  List< Long> ids = ${cdName}DAOFactory.get${name?cap_first}DAO().selectAllIds${name}();
  if (!ids.isEmpty()) {
    Optional<${name}> opt = ${cdName}DAOFactory.get${name?cap_first}DAO().select${name}(ids.get(0));
    if (opt.isPresent()) {
      ${name?uncap_first}Instance = opt.get();
      _doAddRequiredObserver${name}(${name?uncap_first}Instance);
    }
  }
}
if (${name?uncap_first}Instance == null) { // Init new object
  ${name}Builder builder = this._do${name}BuilderFromId(this._doGetFreeGemId());
  if (builder.build().isEmpty())
    throw new RuntimeException(!builder.isBuildable()
        ? "Singleton ${name} could not be built - make sure all variables are assigned "
            + "a default value in the model, or override the builders constructor"
        : "Singleton ${name} was used before being initialized - override the builders constructor");

  ${name?uncap_first}Instance = builder.build().get();
  _doAddRequiredObserver${name}(${name?uncap_first}Instance);
  ${managerName?uncap_first}Observers.forEach(obs -> obs.maybeNotify${name}Added(${name?uncap_first}Instance, NotificationScope.ALL));
}
return  ${name?uncap_first}Instance;
