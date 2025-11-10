/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

import de.rwth.armin.modeling.autopilot.common.*;

component MotionPlanning {
  port
    in  double                                                       currentDirectionX,
    in  double                                                       currentDirectionY,
    in  double                                                       desiredDirectionX,
    in  double                                                       desiredDirectionY,
    in  double                                                       signedDistanceToTrajectory,
    in  double                          currentVelocity,
    in  double                        desiredVelocity,

    out double                                                       engine,
    out double                                                       steering,
    out double                                                       brakes;

 KeepDirection                                        keepDirection;
 SteeringAngleCorrection                              sac;
 Sum                                                  sum1;
 CalculateEngineAndBrakes                             engineAndBrakes;
 EnsureBoundsForSteeringAngle                         boundsSteering;
 EnsureBoundsForBrakes                                boundsBrakes;
 EnsureBoundsForEngine                                boundsEngine;

 currentDirectionX                     ->              keepDirection.currentDirectionX;
 currentDirectionY                     ->              keepDirection.currentDirectionY;
 desiredDirectionX                     ->              keepDirection.desiredDirectionX;
 desiredDirectionY                     ->              keepDirection.desiredDirectionY;

 keepDirection.steeringAngle           ->              sum1.t1;
 signedDistanceToTrajectory            ->              sac.signedDistanceToTrajectory;
 sac.steeringAngleCorrection           ->              sum1.t2;

  // brakes and engine
 currentVelocity                       ->              engineAndBrakes.currentVelocity;
 desiredVelocity                       ->              engineAndBrakes.desiredVelocity;

  // compose output
 engineAndBrakes.engine                ->              boundsEngine.input;
 boundsEngine.output                   ->              engine;

 sum1.result                           ->              boundsSteering.input;
 boundsSteering.output                 ->              steering;

 engineAndBrakes.brakes                ->              boundsBrakes.input;
 boundsBrakes.output                   ->              brakes;
}
