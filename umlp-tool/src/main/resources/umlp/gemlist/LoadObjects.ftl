<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("type", "manager")}
${cd4c.method("protected java.util.List<LoadResult<${type}>> loadObjects(java.util.List<Long> gemIDs)")}
// load the elements with gemID from input list using manager and return the result as list
java.util.List<LoadResult<${type}>> loadResults = new java.util.ArrayList<>();
for(long gemID : gemIDs) {
  // todo load in a way that keeps the information, why in could not be loaded
  // does the manager have such an access point
  java.util.Optional<${type}> objOpt = ${manager}.${type?uncap_first}Opt(gemID);
  if(objOpt.isEmpty()) {
    loadResults.add(new umlp.backendrte.common.LoadResult(umlp.backendrte.common.LoadingState.FAILED_TO_LOAD));
  }
  else {
    loadResults.add(new umlp.backendrte.common.LoadResult(objOpt.get()));
  }
}
return loadResults;
