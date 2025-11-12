/*  https://github.com/MontiCore/monticore */
package de.rwth.armin.modeling.autopilot.common;

component Compass2CurrentDirection {
  port
    in  double               compass,
    out double       currentDirectionX,
    out double       currentDirectionY;

  
}
