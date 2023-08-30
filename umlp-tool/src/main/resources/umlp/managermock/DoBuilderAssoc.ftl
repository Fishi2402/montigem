${tc.signature("assocName", "targetName")}
${cd4c.method("protected  GemList<${targetName}> _doCreate${assocName}Builder(long gemID, List<Long> initialIds)")}
${assocName}GemList list = new ${assocName?capFirst}GemList(gemID);
list._internal_add(0, initialIds);
return list;
