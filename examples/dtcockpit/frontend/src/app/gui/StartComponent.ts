/* (c) https://github.com/MontiCore/monticore */

import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DtcockpitManager } from '@src/dtcockpit/DtcockpitManager';
import { StartComponentTOP } from '@src/gui/StartComponentTOP';
import { GemLineChartData, GemLineChartEntry } from '@umlp/charts';
import { CommandManager } from '@umlp/common';
import { DtcockpitSecurityManager } from "../dtcockpit/DtcockpitSecurityManager";
import { GetCurrentUserRequest } from "../security/GetCurrentUserRequest";

@Component({
  selector: 'start',
  templateUrl: '../../../build/frontendGen/build/appgui/gui/Start.html',
  styles: [":host{ display: flex; flex-direction: row }"],
  styleUrls: []
})

export class StartComponent extends StartComponentTOP {

  protected cache: {
    serverChart: { hash: string, data: GemLineChartData }
  } = { serverChart: { hash: null, data: null } };


  public constructor(route: ActivatedRoute, protected router: Router) {
    super(route, router);
    this.router = router;
  }

  ngOnInit() {
    if (!(DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn())) {
      this.router.navigate(['gui', 'login', 'LoginPage']);
      return;
    }

    CommandManager.executeCommand(new GetCurrentUserRequest()).then(res => {
      let sr = res.asSimpleResult();

      if (sr.isSuccessful()) {
        console.log("GemId of current user is", sr.get());
        DtcockpitManager.getUserSure(sr.get()).then(usr => {
          DtcockpitSecurityManager.signIn(usr);
          console.log("User login complete.");
        });
      } else {
        console.error("Can not get GemId of current user");
      }
    });
    super.ngOnInit();
  }

  public override getServerChartData(): GemLineChartData {
    if (!this.getServers()?.size()) {
      return null;
    }
    const chartEntries: GemLineChartEntry[] = [];

    // fill x axis
    const tmp: Set<string> = new Set();
    for (const s of this.getServers()) {
      for (const ss of s.getServerStatus()) {
        tmp.add(this.formatTime(ss.getTime()));
      }
    }
    let xAxis: string[] = Array.from(tmp).sort();

    const m: Map<string, { gemColor: string, data: number[] }> = new Map();
    for (const s of this.getServers()) {
      const label = s.getHostname();
      // gemId to color
      let gemColor = s.getGemId().toString(16)
      while (gemColor.length < 6) {
        gemColor = gemColor + '0';
      }
      gemColor = `#${gemColor}`;

      if (!m.has(label)) {
        m.set(label, { gemColor, data: Array(xAxis.length).fill(null) });
      }

      for (const ss of s.getServerStatus()) {
        m.get(label).data[xAxis.indexOf(this.formatTime(ss.getTime()))] = ss.getRamUsage();
      }
    }

    for (const [key, value] of m) {
      let entry = new GemLineChartEntry();
      entry.label = key;
      entry.data = value.data;
      entry.borderColor = value.gemColor;
      chartEntries.push(entry);
    }

    const result = new GemLineChartData(chartEntries, xAxis);
    const hash = JSON.stringify(result);
    if (this.cache.serverChart.hash != hash) {
      this.cache.serverChart.hash = hash;
      this.cache.serverChart.data = result;
    }
    return this.cache.serverChart.data;
  }

  formatTime(time: Date): string {
    return time.toLocaleTimeString('de-DE', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    })
  }
}


