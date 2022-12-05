<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourcename","targetname","definition")}

${cd4c.method("protected StatusResult exchange${targetname}()")}

boolean ${sourcename}Loaded = ${definition}Manager.isLoaded${sourcename}(${sourcename}GemId);
boolean old${targetname}Loaded = ${definition}Manager.isLoaded${targetname}(old${targetname}GemId);
boolean new${targetname}Loaded = ${definition}Manager.isLoaded${targetname}(${targetname}GemId);

if(${sourcename}Loaded){
  ${sourcename} ${sourcename} = ${definition}Manager.get${sourcename}Sure(${sourcename}GemId);
  // Will automatically change other side of the association
  ${sourcename}.setVisitedId(indexIn${sourcename}List, ${targetname}GemId, NotificationScope.LOCAL);
} else if(old${targetname}Loaded){
  ${targetname} old${targetname} = ${definition}Manager.get${targetname}Sure(old${targetname}GemId);
  // Does not change other side
  old${targetname}.removeVisitor(indexIn${targetname}List, NotificationScope.MINIMAL);
} else {
  return StatusResult.error(getId());
}

if(new${targetname}Loaded){
  ${targetname} new${targetname} = ${definition}Manager.get${targetname}Sure(${targetname}GemId);
  new${targetname}.addVisitorId(new${targetname}.sizeVisitors(), ${sourcename}GemId, NotificationScope.MINIMAL);
}

return StatusResult.ok(getId());