/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior2;

component BehaviorGeneration {
  port
    in  double                     currentPositionX,
    in  double                     currentPositionY,
    in  double                     currentDirectionX,
    in  double                     currentDirectionY,
    in  int                                   plannedTrajectoryLength,
    in  double            plannedTrajectoryX,
    in  double            plannedTrajectoryY,

    out double                     desiredDirectionX,
    out double                     desiredDirectionY,
    out double                     signedDistanceToTrajectory,
    out double             desiredVelocity;

 TrimPath                                  trimPath;
 CalculateMotionCommands                   calcMotionCmds;

 currentPositionX                             ->       trimPath.currentPositionX;
 currentPositionY                             ->       trimPath.currentPositionY;
 plannedTrajectoryLength                      ->       trimPath.plannedTrajectoryLength;
 plannedTrajectoryX                           ->       trimPath.plannedTrajectoryX;
 plannedTrajectoryY                           ->       trimPath.plannedTrajectoryY;

 currentPositionX                             ->       calcMotionCmds.currentPositionX;
 currentPositionY                             ->       calcMotionCmds.currentPositionY;
 currentDirectionX                            ->       calcMotionCmds.currentDirectionX;
 currentDirectionY                            ->       calcMotionCmds.currentDirectionY;
 trimPath.trimmedTrajectoryLength             ->       calcMotionCmds.trimmedTrajectoryLength;
 trimPath.trimmedTrajectoryX                  ->       calcMotionCmds.trimmedTrajectoryX;
 trimPath.trimmedTrajectoryY                  ->       calcMotionCmds.trimmedTrajectoryY;

 calcMotionCmds.desiredDirectionX             ->       desiredDirectionX;
 calcMotionCmds.desiredDirectionY             ->       desiredDirectionY;
 calcMotionCmds.signedDistanceToTrajectory    ->       signedDistanceToTrajectory;
 calcMotionCmds.desiredVelocity               ->       desiredVelocity;
}
