/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

component CalculatePidError {
  port
    in double  currentVelocity,
    in double  desiredVelocity,
    out double error;

  
}
