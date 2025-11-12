/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior;

import de.rwth.armin.modeling.autopilot.common.*;

// trims the planned path
// such that the first segment
// is the closest to the current vehicle's position
// and the first point is the projection
// of the current position onto the planned path
component TrimPath {
  port
    in  double                 currentPositionX,
    in  double                 currentPositionY,
    in  int                               plannedTrajectoryLength,
    in  double        plannedTrajectoryX,
    in  double        plannedTrajectoryY,

    out int                               trimmedTrajectoryLength,
    out double        trimmedTrajectoryX,
    out double        trimmedTrajectoryY;

  
}
