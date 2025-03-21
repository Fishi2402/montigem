/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

component SteeringAngleCorrection {
  port
    in double signedDistanceToTrajectory,
    out double steeringAngleCorrection;

  
}
