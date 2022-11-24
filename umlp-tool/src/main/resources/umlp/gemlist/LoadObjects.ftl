<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("type", "manager")}
${cd4c.method("protected java.util.List<umlp.rte.LoadResult<${type}>> loadObjects(java.util.List<Long> gemIDs)")}
// load the elements with gemID from input list using manager and return the result as list
java.util.List<umlp.rte.LoadResult<${type}>> loadResults = new java.util.ArrayList<>();
for(long gemID : gemIDs) {
  // todo load in a way that keeps the information, why in could not be loaded
  // does the manager have such an access point
  java.util.Optional<${type}> objOpt = ${manager}.${type?uncap_first}Opt(gemID);
  if(objOpt.isEmpty()) {
    loadResults.add(new umlp.rte.LoadResult(umlp.rte.LoadingState.FAILED_TO_LOAD));
  }
  else {
    loadResults.add(new umlp.rte.LoadResult(objOpt.get()));
  }
}
return loadResults;
