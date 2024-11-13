/* (c) https://github.com/MontiCore/monticore */


import {GemButton, GemCheckbox, GemImage, GemLink, GemText} from "@umlp/basic";
import {GemCard, GemColumn, GemColumns,  GemRow, GemRows} from "@umlp/arrange";

import {WizardContainer} from "@src/wizard/WizardContainer";

interface InspectData {
  [key: string]: {
    format: string,
    description?: string
  }
}

export interface WizData {
  inspect: InspectData;
  new: [any, () =>  { [key: string]: any }]; // constructor
  componentName: string;
}


export const WIZARD_DATA: { [name: string]: WizData } = {
  'GemText': {
    componentName: 'GemText',
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
    new: [GemText, () => {
      return {value: 'Hello there'}
    }]
  },

  'GemButton': {
    componentName: 'GemButton',
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      label: {format: "string"}, // content skipped
      disabled: {format: "boolean"},
    },
    new: [GemButton, () => {
      return {label: 'GemButton'}
    }]
  },
  'GemLink': {
    componentName: 'GemLink',
    inspect: {
      url: {format: "string"},
      text: {format: "string"},
    },
    new: [GemLink, () => {
      return {text: 'Link', url: 'https://www.youtube.com/watch?v=dQw4w9WgXcQ'}
    }]
  },
  'GemCheckbox': {
    componentName: 'GemCheckbox',
    inspect: {
      checkboxItem: {format: "string"},
    },
    new: [GemCheckbox, () => {
      return {checkboxItem: 'GemCheckbox'}
    }]
  },
  'GemImage': {
    componentName: 'GemImage',
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      source: {format: "string"},
      alt: {format: "string"},
    },
    new: [GemImage, () => {
      return {source: 'https://www.se-rwth.de/assets/img/icons/rwth_se_rgb.png'}
    }]
  },
  'GemRow': {
    componentName: 'GemRow',
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      hAlign: {format: "string"},
      vAlign: {format: "string"},
      rowGap: {format: "string"},
      colGap: {format: "string"},
      wrap: {format: "string"},
    },
    new: [WizardContainer, () => {
      return {
        containerType: GemRow,
        humanReadableName: "GemRow",
        containerInputs: {},
      };
    }]
  },
  'GemRows': {
    componentName: 'GemRows',
    inspect: {
      height: {format: "string"},
    },
    new: [WizardContainer, () => {
      return {
        containerType: GemRows,
        humanReadableName: "GemRows",
        containerInputs: {},
      };
    }]
  },
  'GemColumn': {
    componentName: 'GemColumn',
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
      hAlign: {format: "string"},
      vAlign: {format: "string"},
      rowGap: {format: "string"},
      colGap: {format: "string"},
      wrap: {format: "string"},
    },
    new: [WizardContainer, () => {
      return {
        containerType: GemColumn,
        humanReadableName: "GemColumn",
        containerInputs: {},
      };
    }]
  },
  'GemColumns': {
    componentName: 'GemColumns',
    inspect: {
    },
    new: [WizardContainer, () => {
      return {
        containerType: GemColumns,
        humanReadableName: "GemColumns",
        containerInputs: {},
        innerNames: {"column1": -1, "column2": -1, "column3": -1}
      };
    }]
  },
  'GemCard': {
    componentName: 'GemCard',
    inspect: {
      height: {format: "string"},
      width: {format: "string"},
    },
    new: [WizardContainer, () => {
      return {
        containerType: GemCard,
        humanReadableName: "GemCard",
        containerInputs: {'title': 'Test'},
        innerNames: {"titleComponent": 1, "component": 1}
      };
    }]
  },
  // 'GemDialog': { // Not really working due to ?open
  //   componentName: 'GemDialog',
  //   inspect: {
  //     enableBackdrop : {format: "boolean"},
  //     top: {format: "string"},
  //     left: {format: "string"},
  //     isOpen : {format: "boolean"},
  //   },
  //   new: [WizardContainer, () => {
  //     return {
  //       containerType: GemDialog,
  //       humanReadableName: "GemDialog",
  //       containerInputs: {},
  //       innerNames: { "component": 1}
  //     };
  //   }]
  // },
  // 'GemGrid (List of List not supported!)': {
  //   componentName: 'GemGrid',
  //   inspect: {
  //     height: {format: "string"},
  //     width: {format: "string"},
  //     hAlign: {format: "string"},
  //     vAlign: {format: "string"},
  //     rowGap: {format: "string"},
  //     colGap: {format: "string"},
  //     x: {format: "string[]"},
  //     y: {format: "string[]"},
  //   },
  //   new: [WizardContainer, () => {
  //     return {
  //       containerType: GemGrid,
  //       humanReadableName: "GemGrid (List of List not supported?)",
  //       containerInputs: {},
  //       innerNames: {"rows": -1, "cols": -1, "cells": -1}
  //     };
  //   }]
  // },
}

export function GET_WIZARD_DATA_BY_CLASS (clazz: any) : WizData {
  for (let key in WIZARD_DATA) {
    if (WIZARD_DATA[key].new[0] == WizardContainer && WIZARD_DATA[key].new[1]()['containerType'] == clazz) {
      return WIZARD_DATA[key];
    }
    if (WIZARD_DATA[key].new[0] === clazz)
      return WIZARD_DATA[key];
  }
  return undefined;
}
