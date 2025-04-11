/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior;

import de.rwth.armin.modeling.autopilot.common.*;

component Distance2Velocity {
  port
    in  double                distance,
    in  double        minVelocity,
    in  double        maxVelocity,

    out double        velocity;

  
}
