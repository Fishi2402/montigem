package coveragedashboard.service;

import coveragedashboard.service.dto.RunIngestionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST endpoint for ingesting CI/CD coverage artifact bundles and querying ingested runs.
 * Effective base path is {@code /umlp/api/v1/runs} (via {@code server.servlet.context-path}).
 */
@RestController
@RequestMapping("/v1/runs")
public class IngestionEndpoint {

  private final RunIngestionService runIngestionService;

  public IngestionEndpoint(RunIngestionService runIngestionService) {
    this.runIngestionService = runIngestionService;
  }

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<RunIngestionResponse> ingestRun(
      @RequestPart("artifact") MultipartFile artifact) {
    RunIngestionResponse response = runIngestionService.ingestArchive(artifact);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{runId}")
        .buildAndExpand(response.runId())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping(
      value = "/{runId}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<RunIngestionResponse> getRun(@PathVariable long runId) {
    return runIngestionService.getRunSummary(runId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
