package dtcockpit.service;

import dtcockpit.DtcockpitManager;
import dtcockpit.Server;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/server_status")
public class ServerStatusEndpoint {


  @PostMapping("/")
  public ResponseEntity<String> processJson(@RequestBody ExternalServerStatus data) {
    String hostname = data.hostname;
    if(hostname == null || hostname.isBlank()){
      return ResponseEntity.badRequest().build();
    }

    Server server = null;

    for (Server s : DtcockpitManager.getServerList()) {
      if(s.getHostname().equals(hostname)){
        server = s;
        break;
      }
    }

    if(server == null){
      server = DtcockpitManager.serverBuilder().serverStatus(Set.of()).hostname(hostname).build().get();
    }

    server.addServerStatus(DtcockpitManager.serverStatusBuilder()
        .time(data.time)
        .diskUsage(data.diskUsage)
        .maxDisk(data.maxDisk)
        .ramUsage(data.ramUsage)
        .maxRam(data.maxRam)
        .processorUsage(data.processorUsage)
        .build().get()
    );

    return ResponseEntity.ok("Ok");
  }


  private static class ExternalServerStatus {
    private String hostname;
    private LocalDateTime time;
    private int diskUsage;
    private int maxDisk;
    private int ramUsage;
    private int maxRam;
    private float processorUsage;

    public String getHostname() {
      return hostname;
    }

    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    public LocalDateTime getTime() {
      return time;
    }

    public void setTime(LocalDateTime time) {
      this.time = time;
    }

    public int getDiskUsage() {
      return diskUsage;
    }

    public void setDiskUsage(int diskUsage) {
      this.diskUsage = diskUsage;
    }

    public int getMaxDisk() {
      return maxDisk;
    }

    public void setMaxDisk(int maxDisk) {
      this.maxDisk = maxDisk;
    }

    public int getRamUsage() {
      return ramUsage;
    }

    public void setRamUsage(int ramUsage) {
      this.ramUsage = ramUsage;
    }

    public int getMaxRam() {
      return maxRam;
    }

    public void setMaxRam(int maxRam) {
      this.maxRam = maxRam;
    }

    public float getProcessorUsage() {
      return processorUsage;
    }

    public void setProcessorUsage(float processorUsage) {
      this.processorUsage = processorUsage;
    }
  }
}
