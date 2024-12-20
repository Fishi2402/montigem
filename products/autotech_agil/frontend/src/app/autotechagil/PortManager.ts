/* (c) https://github.com/MontiCore/monticore */

import { Port } from '@src/autotechagil/Port';

export class PortManager {

  public static getSelectedPort(): Port {
    let selectedPort: Port = new Port();
    Object.assign(selectedPort, JSON.parse(localStorage.getItem('selectedPort')) as Port);
    return selectedPort;
  }

  public static setSelectedPort(value: Port) {
    localStorage.setItem('selectedPort', JSON.stringify(Port))
  }

}


