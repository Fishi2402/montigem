import { AfterViewInit, Component, inject, signal } from '@angular/core';

import { ParseMyDSLComponent, config } from "gui/ParseMyDSLComponent";

import {ParseMyCompilationUnit} from "domain/commands/ParseMyCompilationUnit"
import {CommandManager, Event} from '@umlp/common';

import {ActivatedRoute, Router} from '@angular/router';



@Component({
    standalone: true,
    imports: [
        ... config.imports,
    ],
    selector: config.selector,
    templateUrl: config.templateUrl,
    styles: config.styles,
    styleUrls: config.styleUrls
})
export class ParseMyDSL extends ParseMyDSLComponent {
    gemIdToVisit: number = -1;

    public constructor(route: ActivatedRoute, protected router: Router) {
        // super(route, router);
        super();
        this.router = router;
    }


    public override parse(): Event<void> {
        return () => {
            this.setDisableInput(true);
            this.setDisableParse(true);
            this.setDisableLink(true);
            this.setMsg("Parsing...");
            this.setErrors([]);
            this.gemIdToVisit = -1;

            CommandManager.executeCommand(new ParseMyCompilationUnit(this.parseInput))
                .then(res => {
                  console.warn("Received result", res)
                    this.setDisableInput(false);

                    if (res.isSimpleResult() && res.asSimpleResult().isSuccessful() && res.asSimpleResult().get().isPresentAst()) {
                        this.gemIdToVisit = res.asSimpleResult().get().getAstGem().getGemId();
                        this.setMsg("Successfully parsed!")
                        this.setDisableLink(false);
                    } else {
                        if (res.isSimpleResult() && res.asSimpleResult().isSuccessful())
                          this.setErrors(res.asSimpleResult().get().getErrorsList());
                        this.setDisableParse(false);
                        this.setMsg("Failed!")
                    }
                })
        };
    }

    public override viewAST(): Event<void> {
        return () => {
            if (this.isDisableLink()) return;
            this.router.navigate(['domain', 'ASTMyCompilationUnitDetails', this.gemIdToVisit]);
        };

    };

    public override changeInput(): Event<void> {
            return () => {
                this.setDisableParse(false);
                this.setDisableLink(true);
            };
    };
}
