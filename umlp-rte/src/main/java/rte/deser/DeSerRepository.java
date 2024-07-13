package rte.deser;

import umlp.backendrte.command.JsonDeSer;

import java.util.Map;

public interface DeSerRepository {

  abstract public Map<String, JsonDeSer> getDeSerMap();

}
