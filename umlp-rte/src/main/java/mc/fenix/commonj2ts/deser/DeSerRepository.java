package mc.fenix.commonj2ts.deser;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.JsonDeSer;
import umlp.backendrte.common.BaseClassObjectMapper;

import java.util.Map;
import java.util.Optional;

public interface DeSerRepository {

  abstract public Map<String, JsonDeSer> getDeSerMap();

}
