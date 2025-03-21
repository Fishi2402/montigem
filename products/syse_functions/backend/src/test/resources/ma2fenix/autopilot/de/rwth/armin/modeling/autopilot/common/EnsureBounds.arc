/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.common;

component EnsureBounds {
  port
    in double lowerBound,
    in double upperBound,
    in double input,
    out double output;

  
}
