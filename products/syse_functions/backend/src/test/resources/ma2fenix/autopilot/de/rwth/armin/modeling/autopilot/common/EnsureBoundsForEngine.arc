/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.common;

component EnsureBoundsForEngine {
  port in double input, out double output;

 EnsureBounds eb;
 DoubleConstant dc1, dc2;

 dc1.output -> eb.lowerBound;
 dc2.output -> eb.upperBound;
 input -> eb.input;
 eb.output -> output;
}
