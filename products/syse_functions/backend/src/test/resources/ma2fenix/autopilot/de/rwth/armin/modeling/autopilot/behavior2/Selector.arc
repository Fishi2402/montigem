/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior2;

component Selector {
  port
    in  boolean                                        isDriveToFirstPosition,
    in  double                 desiredDirectionX1,
    in  double                 desiredDirectionY1,
    in  double                 signedDistanceToTrajectory1,
    in  double         desiredVelocity1,
    in  double                 desiredDirectionX2,
    in  double                 desiredDirectionY2,
    in  double                 signedDistanceToTrajectory2,
    in  double         desiredVelocity2,

    out double                 desiredDirectionX,
    out double                 desiredDirectionY,
    out double                 signedDistanceToTrajectory,
    out double         desiredVelocity;

  
}
