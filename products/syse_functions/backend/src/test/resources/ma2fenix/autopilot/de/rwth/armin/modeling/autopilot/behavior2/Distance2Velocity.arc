/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior2;

component Distance2Velocity {
  port
    in  double                distance,
    in  double        minVelocity,
    in  double        maxVelocity,

    out double        velocity;

  
}
