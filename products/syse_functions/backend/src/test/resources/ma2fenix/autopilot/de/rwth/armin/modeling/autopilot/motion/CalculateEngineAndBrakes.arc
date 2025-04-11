/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.motion;

import de.rwth.armin.modeling.autopilot.common.*;

component CalculateEngineAndBrakes {
  port
    in double currentVelocity,
    in double desiredVelocity,
    out double engine,
    out double brakes;

 CalculatePidParameters pidParams;
 CalculatePidError pidError;
 Abs abs1;
 PID pid;
 DecideEngineOrBrakes engineOrBrakes;

 currentVelocity -> pidParams.currentVelocity;
 desiredVelocity -> pidParams.desiredVelocity;

 currentVelocity -> pidError.currentVelocity;
 desiredVelocity -> pidError.desiredVelocity;

 pidParams.paramP -> pid.paramP;
 pidParams.paramI -> pid.paramI;
 pidParams.paramD -> pid.paramD;
 pidParams.paramDecayCoefficient -> pid.paramDecayCoefficient;

 pidError.error -> abs1.input;
 abs1.output -> pid.error;

 pidError.error -> engineOrBrakes.error;
 pid.control -> engineOrBrakes.controlSignal;

 engineOrBrakes.engine -> engine;
 engineOrBrakes.brakes -> brakes;
}
