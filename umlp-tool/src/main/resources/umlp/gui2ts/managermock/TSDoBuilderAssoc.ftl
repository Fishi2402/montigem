${tc.signature("assocName", "targetName")}
${cd4c.method("public  GemList<${targetName}> _doCreate${assocName}Builder(long gemID, List<Long> initialIds)")}
let list: ${assocName}GemList = new ${assocName?capFirst}GemList(gemID);
list._internal_add(0, initialIds);
return list;
