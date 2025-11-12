/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.behavior2;

component CalculateMotionCommands {
  port
    in  double                 currentPositionX,
    in  double                 currentPositionY,

    in  double                 currentDirectionX,
    in  double                 currentDirectionY,

    in  int                               trimmedTrajectoryLength,
    in  double        trimmedTrajectoryX,
    in  double        trimmedTrajectoryY,

    out double                 desiredDirectionX,
    out double                 desiredDirectionY,
    out double                 signedDistanceToTrajectory,
    out double         desiredVelocity;

 IsDriveToFirstPosition                isDriveToFirstPosition;
 DriveToFirstPosition                  driveToFirstPosition;
 Distance2Velocity                     d2v1;
 FollowTrajectory                      followTrajectory;
 Distance2Velocity                     d2v2;
 Selector                              selector;

 currentPositionX                                               ->     isDriveToFirstPosition.currentPositionX;
 currentPositionY                                               ->     isDriveToFirstPosition.currentPositionY;
 trimmedTrajectoryLength                                        ->     isDriveToFirstPosition.trimmedTrajectoryLength;
 trimmedTrajectoryX                                             ->     isDriveToFirstPosition.trimmedTrajectoryX;
 trimmedTrajectoryY                                             ->     isDriveToFirstPosition.trimmedTrajectoryY;

 isDriveToFirstPosition.result                                  ->     driveToFirstPosition.isDriveToFirstPosition;
 currentPositionX                                               ->     driveToFirstPosition.currentPositionX;
 currentPositionY                                               ->     driveToFirstPosition.currentPositionY;
 trimmedTrajectoryLength                                        ->     driveToFirstPosition.trimmedTrajectoryLength;
 trimmedTrajectoryX                                             ->     driveToFirstPosition.trimmedTrajectoryX;
 trimmedTrajectoryY                                             ->     driveToFirstPosition.trimmedTrajectoryY;
                                                                         
 driveToFirstPosition.distance                                  ->     d2v1.distance;
 driveToFirstPosition.minVelocity                               ->     d2v1.minVelocity;
 driveToFirstPosition.maxVelocity                               ->     d2v1.maxVelocity;
                                                                         
 isDriveToFirstPosition.result                                  ->     followTrajectory.isDriveToFirstPosition;
 currentPositionX                                               ->     followTrajectory.currentPositionX;
 currentPositionY                                               ->     followTrajectory.currentPositionY;
 trimmedTrajectoryLength                                        ->     followTrajectory.trimmedTrajectoryLength;
 trimmedTrajectoryX                                             ->     followTrajectory.trimmedTrajectoryX;
 trimmedTrajectoryY                                             ->     followTrajectory.trimmedTrajectoryY;

 followTrajectory.distance                                      ->     d2v2.distance;
 followTrajectory.minVelocity                                   ->     d2v2.minVelocity;
 followTrajectory.maxVelocity                                   ->     d2v2.maxVelocity;

 isDriveToFirstPosition.result                                  ->     selector.isDriveToFirstPosition;
 driveToFirstPosition.desiredDirectionX                         ->     selector.desiredDirectionX1;
 driveToFirstPosition.desiredDirectionY                         ->     selector.desiredDirectionY1;
 driveToFirstPosition.signedDistanceToTrajectory                ->     selector.signedDistanceToTrajectory1;
 d2v1.velocity                                                  ->     selector.desiredVelocity1;
 followTrajectory.desiredDirectionX                             ->     selector.desiredDirectionX2;
 followTrajectory.desiredDirectionY                             ->     selector.desiredDirectionY2;
 followTrajectory.signedDistanceToTrajectory                    ->     selector.signedDistanceToTrajectory2;
 d2v2.velocity                                                  ->     selector.desiredVelocity2;

 selector.desiredDirectionX                                     ->     desiredDirectionX;
 selector.desiredDirectionY                                     ->     desiredDirectionY;
 selector.signedDistanceToTrajectory                            ->     signedDistanceToTrajectory;
 selector.desiredVelocity                                       ->     desiredVelocity;
}
