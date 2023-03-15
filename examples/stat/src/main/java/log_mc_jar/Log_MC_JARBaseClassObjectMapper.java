/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar;

import log_mc_jar.custom_deser.DurationDeSer;
import log_mc_jar.custom_deser.InetAddressDeSer;
import log_mc_jar.custom_deser.InstantDeSer;

public class Log_MC_JARBaseClassObjectMapper extends Log_MC_JARBaseClassObjectMapperTOP {
  public Log_MC_JARBaseClassObjectMapper() {
    this.baseDeSers.put("java.time.Duration", new DurationDeSer());
    this.baseDeSers.put("java.time.Instant", new InstantDeSer());
    this.baseDeSers.put("java.net.InetAddress", new InetAddressDeSer());
  }
}
