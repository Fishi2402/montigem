/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

component DecideEngineOrBrakes {
  port
    in double error,
    in double controlSignal,
    out double engine,
    out double brakes;

  
}
