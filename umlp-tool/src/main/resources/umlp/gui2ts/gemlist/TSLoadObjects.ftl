<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("type", "manager")}
${cd4c.method("protected java.util.List<Promise<LoadResult<${type}>>> loadObjects(java.util.List<Long> gemIDs)")}
// load the elements with gemID from input list using manager and return the result as list
let loadResults: Array<Promise<LoadResult<${type}>>> = [];
for(let gemID of gemIDs) {
  // todo load in a way that keeps the information, why in could not be loaded
  // does the manager have such an access point
  loadResults.push(
    ${manager}.${type?uncap_first}Opt(gemID).then(v => {
      let objOpt = v;
      if(objOpt == null) {
        return new LoadResult<${type}>().LoadResult2(LoadingState.FAILED_TO_LOAD);
      }
      else {
        return new LoadResult<${type}>().LoadResult1(objOpt);
      }
    })
  )
}
return loadResults;
