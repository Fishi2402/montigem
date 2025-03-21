/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior;

component IsDriveToFirstPosition {
  port
    in  double                 currentPositionX,
    in  double                 currentPositionY,

    in  int                               trimmedTrajectoryLength,
    in  double        trimmedTrajectoryX,
    in  double        trimmedTrajectoryY,

    out boolean                                        result;

  
}
