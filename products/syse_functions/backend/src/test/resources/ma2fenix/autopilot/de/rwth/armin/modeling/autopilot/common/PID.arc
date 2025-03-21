/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.common;

component PID {
  port
    in double paramP,
    in double paramI,
    in double paramD,
    in double paramDecayCoefficient,
    in double error,
    out double control;

  
}
