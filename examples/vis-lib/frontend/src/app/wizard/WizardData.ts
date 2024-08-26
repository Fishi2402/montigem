/* (c) https://github.com/MontiCore/monticore */


import {GemButton, GemText} from "@umlp/basic";
import {GemCard, GemColumn, GemRow} from "@umlp/arrange";

import {WizardContainer} from "@src/wizard/WizardContainer";

interface InspectData {
  [key:string]: {
    format: string,
    description?: string
  }
}

export interface WizData {
  inspect: InspectData;
  new: [any, {[key:string]:any}] // constructor
}


export const WIZARD_DATA: {[name:string]: WizData} = {
  'GemText': {
    inspect: {
      value: {format: "string"},
      color: {format: "string"},
      size: {format: "string"},
      fontStyle: {format: "string"},
      weight: {format: "string"},
      fontFamily: {format: "string"},
      width: {format: "string"},
      height: {format: "string"},
    },
    new: [GemText, {value: 'Hello there'}]
  },
  'GemButton': {
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      label: {format: "string"}, // content skipped
      disabled: {format: "boolean"},
    },
    new: [GemButton, {value: 'GemButton'}]
  },
  'GemRow': {
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      hAlign: {format: "string"},
      vAlign: {format: "string"},
      rowGap: {format: "string"},
      colGap: {format: "string"},
      wrap: {format: "string"},
    },
    new: [WizardContainer, {
      containerType: GemRow,
      humanReadableName: "GemRow",
      containerInputs: {},
    }]
  },
  'GemColumn': {
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      hAlign: {format: "string"},
      vAlign: {format: "string"},
      rowGap: {format: "string"},
      colGap: {format: "string"},
      wrap: {format: "string"},
    },
    new: [WizardContainer, {
      containerType: GemColumn,
      humanReadableName: "GemColumn",
      containerInputs: {},
    }]
  },
  'GemCard': {
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
    },
    new: [WizardContainer, {
      containerType: GemCard,
      humanReadableName: "GemCard",
      containerInputs: {'title': 'Test'},
      innerNames: {"titleComponent": 1, "component": 1}
    }]
  },
}
