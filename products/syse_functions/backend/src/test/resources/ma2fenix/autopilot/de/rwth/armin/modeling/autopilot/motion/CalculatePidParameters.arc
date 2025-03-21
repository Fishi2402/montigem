/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

component CalculatePidParameters {
  port
    in  double       currentVelocity,
    in  double       desiredVelocity,

    out double       paramP,
    out double       paramI,
    out double       paramD,
    out double       paramDecayCoefficient;

  
}
