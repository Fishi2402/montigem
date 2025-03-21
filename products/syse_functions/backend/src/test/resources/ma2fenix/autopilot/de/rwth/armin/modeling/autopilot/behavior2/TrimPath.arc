/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior2;

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
