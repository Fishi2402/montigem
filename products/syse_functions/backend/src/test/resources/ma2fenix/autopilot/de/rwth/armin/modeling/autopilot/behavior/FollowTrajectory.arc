/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior;

component FollowTrajectory {
  port
    in  boolean                                        isDriveToFirstPosition,
    in  double                 currentPositionX,
    in  double                 currentPositionY,
    in  int                               trimmedTrajectoryLength,
    in  double        trimmedTrajectoryX,
    in  double        trimmedTrajectoryY,

    out double                 desiredDirectionX,
    out double                 desiredDirectionY,
    out double                 signedDistanceToTrajectory,
    out double         minVelocity,
    out double         maxVelocity,
    out double         distance;

  
}
