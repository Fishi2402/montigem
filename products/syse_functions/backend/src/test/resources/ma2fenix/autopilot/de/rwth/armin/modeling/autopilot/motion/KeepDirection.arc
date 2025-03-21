/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

import de.rwth.armin.modeling.autopilot.common.SignedAngleBetween;

component KeepDirection {
  port
    in  double                               currentDirectionX,
    in  double                               currentDirectionY,
    in  double                               desiredDirectionX,
    in  double                               desiredDirectionY,

    out double                               steeringAngle;

 SignedAngleBetween           sab;

 currentDirectionX     ->      sab.v1x;
 currentDirectionY     ->      sab.v1y;
 desiredDirectionX     ->      sab.v2x;
 desiredDirectionY     ->      sab.v2y;
 sab.angle             ->      steeringAngle;
}
