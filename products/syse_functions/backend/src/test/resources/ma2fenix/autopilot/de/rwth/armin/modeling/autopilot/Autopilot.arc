/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot;

import de.rwth.armin.modeling.autopilot.common.*;
import de.rwth.armin.modeling.autopilot.behavior.*;
import de.rwth.armin.modeling.autopilot.motion.*;

component Autopilot {
  port
    // length of simulation time frame
    // e.g. when simulation frequency is 200 Hz
    // this value will be equal to 5ms
//    in  double               timeIncrement,

    // current velocity
    in  double           currentVelocity,

    // current vehicle's position
    in  double                 x,
    in  double                 y,

    // orientation angle  in radians
    // it is measured from from the positive Y axis direction
    // to the actual vehicle's direction counter-clockwise
    // Example 1: vehicle has direction
    // compass = 0.25 * PI
    // Example 2: vehicle has direction  i.e. it is directed along the X axis
    // compass = -0.5 * PI
    in  double                      compass,

    // current engine actuation
//    in  double                     currentEngine,

    // current steering actuation
    // negative value: left turn
    // positive value: right turn
//    in  double                currentSteering,

    // current brakes actuation
//    in  double                     currentBrakes,

    // planned trajectory
    // represented by two arrays trajectory_x[] and trajectory_y[]
    // which both have length trajectory_length
    in  int                               trajectory_length,
    in  double        trajectory_x,
    in  double        trajectory_y,

    // output actuation commands
    out double                     engine,
    out double                steering,
    out double                     brakes;

   Compass2CurrentDirection            c2cd;
   BehaviorGeneration                  behaviorGeneration;
   MotionPlanning                      motionPlanning;

   compass                                          ->      c2cd.compass;

   x                                                ->      behaviorGeneration.currentPositionX;
   y                                                ->      behaviorGeneration.currentPositionY;
   trajectory_length                                ->      behaviorGeneration.plannedTrajectoryLength;
   trajectory_x                                     ->      behaviorGeneration.plannedTrajectoryX;
   trajectory_y                                     ->      behaviorGeneration.plannedTrajectoryY;
   c2cd.currentDirectionX                           ->      behaviorGeneration.currentDirectionX;
   c2cd.currentDirectionY                           ->      behaviorGeneration.currentDirectionY;

   currentVelocity                                  ->      motionPlanning.currentVelocity;
   c2cd.currentDirectionX                           ->      motionPlanning.currentDirectionX;
   c2cd.currentDirectionY                           ->      motionPlanning.currentDirectionY;
   behaviorGeneration.desiredDirectionX             ->      motionPlanning.desiredDirectionX;
   behaviorGeneration.desiredDirectionY             ->      motionPlanning.desiredDirectionY;
   behaviorGeneration.signedDistanceToTrajectory    ->      motionPlanning.signedDistanceToTrajectory;
   behaviorGeneration.desiredVelocity               ->      motionPlanning.desiredVelocity;

   motionPlanning.engine                            ->      engine;
   motionPlanning.steering                          ->      steering;
   motionPlanning.brakes                            ->      brakes;
}
