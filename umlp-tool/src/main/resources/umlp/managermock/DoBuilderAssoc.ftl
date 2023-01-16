${tc.signature("assocName", "sourceName", "targetName")}
${cd4c.method("protected  GemList<${targetName}> _doCreate${assocName?capFirst}Builder(long gemID, List<Long> initialIds)")}
${sourceName}_${assocName?capFirst}GemList list = new ${sourceName}_${assocName?capFirst}GemList(gemID, initialIds);
return list;
