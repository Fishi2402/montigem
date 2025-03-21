/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.basicComp;

component PID{
port
 in double desiredVel,
 in double currentVel,
 in double timeInc,

 out double engineVal,
 out double breakesVal;

 

}
