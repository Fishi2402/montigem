<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "domainPackage", "isSingleton", "isAbstract")}
${cd4c.method("public List<${classname}> _doLoadAll${classname}WithIDs(List<Long> gemIDs)")}
<#if isSingleton || isAbstract>
  java.util.Set<Long> idSet = new java.util.HashSet<>();
  for(Long id : gemIDs) {
    idSet.add(id);
  }
  List<${classname}> result = new java.util.ArrayList<>();
  for (${classname} item : this._doGet${classname}List()) {
    if (idSet.contains(item.getGemId())) {
      result.add(item);
    }
  }
  return result;
<#else>
  List<Long> newGemIDs = new ArrayList<>();
  Set<Long> longs = this.known${classname}s.keySet();
  for (Long gemID : gemIDs) {
    if (!longs.contains(gemID)) {
      newGemIDs.add(gemID);
    }
  }

  if (!newGemIDs.isEmpty()) {
    CommandResult<${classname}> commandResult = CommandManager.executeCommand(new LoadAll${classname}WithIDs(newGemIDs));

    if (commandResult.isListResult()) {
      List<${classname}> items = commandResult.asListResult().get();
      for (${classname} item : items) {
        if (!item.containsObserver(the${classname}CommandObserver)) {
          item.addObserver(the${classname}CommandObserver);
        }
        Log.trace("Adding known ${classname} " + item.getGemId(), LogUtil.getLoggerName());
        known${classname}s.put(item.getGemId(), item);
      }
    } else {
      Log.error("0xCL001 Could not load ${classname} batch. Expected ListResult.");
    }
  }

  List<${classname}> result = new ArrayList<>();
  for (Long gemID : gemIDs) {
    if (known${classname}s.containsKey(gemID)) {
      result.add(known${classname}s.get(gemID));
    }
  }

  return result;
</#if>
