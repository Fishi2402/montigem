<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("sourceName")}
${cd4c.method("public boolean TESTING_deepEquals(List<${sourceName}> other)")}
if(this == other){
  return true;
}

if(size() != other.size()){
  return false;
}

for(int i = 0; i < size(); i++){
  ${sourceName} mine = get(i);
  ${sourceName} others = other.get(i);
  if(
    (mine == null && others != null)
    || (mine != null && others == null)
    || !mine.TESTING_deepEquals(others)
  ){
    return false;
  }
}
return true;
