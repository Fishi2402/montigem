${tc.signature("assocName", "targetName")}
${cd4c.method("protected  GemList<${targetName}> _doCreate${assocName}Builder(long gemID, List<Long> initialIds)")}
${assocName}GemList list = new ${assocName?capFirst}GemList(gemID, initialIds);
return list;
