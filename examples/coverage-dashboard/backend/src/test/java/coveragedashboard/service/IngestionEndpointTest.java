package coveragedashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coveragedashboard.ReportType;
import coveragedashboard.service.dto.RunIngestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IngestionEndpointTest {

  private RunIngestionService runIngestionService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    runIngestionService = mock(RunIngestionService.class);
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mockMvc = MockMvcBuilders.standaloneSetup(new IngestionEndpoint(runIngestionService))
        .setControllerAdvice(new IngestionExceptionHandler())
        .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
        .build();
  }

  @Test
  void postRunsReturnsCreatedWithLocationAndSummary() throws Exception {
    RunIngestionResponse expected = new RunIngestionResponse(
        42L,
        "abc123",
        "main",
        LocalDateTime.of(2026, 11, 25, 17, 0),
        0.85,
        List.of(new RunIngestionResponse.TestGroupSummary(
            "jsweet-test",
            0.85,
            List.of(new RunIngestionResponse.ReportSummary("Unit Test", ReportType.GENERATOR_UNIT_TEST, 0.85))
        ))
    );
    when(runIngestionService.ingestArchive(any())).thenReturn(expected);

    MockMultipartFile artifact = new MockMultipartFile(
        "artifact", "bundle.zip", "application/zip", new byte[]{1, 2, 3});

    mockMvc.perform(multipart("/v1/runs").file(artifact))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/v1/runs/42"))
        .andExpect(jsonPath("$.runId").value(42))
        .andExpect(jsonPath("$.commitHash").value("abc123"))
        .andExpect(jsonPath("$.branchName").value("main"))
        .andExpect(jsonPath("$.cci").value(0.85))
        .andExpect(jsonPath("$.testGroups[0].name").value("jsweet-test"))
        .andExpect(jsonPath("$.testGroups[0].cci").value(0.85))
        .andExpect(jsonPath("$.testGroups[0].reports[0].type").value("GENERATOR_UNIT_TEST"))
        .andExpect(jsonPath("$.testGroups[0].reports[0].coverageValue").value(0.85));
  }

  @Test
  void postRunsReturnsBadRequestProblemDetailOnInvalidArchive() throws Exception {
    when(runIngestionService.ingestArchive(any()))
        .thenThrow(new InvalidIngestionRequestException("Archive must contain a 'manifest.json' file at its root."));

    MockMultipartFile artifact = new MockMultipartFile(
        "artifact", "bundle.zip", "application/zip", new byte[]{1, 2, 3});

    mockMvc.perform(multipart("/v1/runs").file(artifact))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Invalid Coverage Artifact"))
        .andExpect(jsonPath("$.detail").value("Archive must contain a 'manifest.json' file at its root."));
  }

  @Test
  void postRunsReturnsBadRequestWhenArtifactPartIsMissing() throws Exception {
    mockMvc.perform(multipart("/v1/runs").contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Missing Required Multipart Field"));
  }

  @Test
  void getRunReturnsOkWhenFound() throws Exception {
    RunIngestionResponse expected = new RunIngestionResponse(
        7L,
        "def456",
        "feature/test",
        LocalDateTime.of(2026, 11, 25, 17, 5),
        0.75,
        List.of()
    );
    when(runIngestionService.getRunSummary(7L)).thenReturn(Optional.of(expected));

    mockMvc.perform(get("/v1/runs/7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.runId").value(7))
        .andExpect(jsonPath("$.commitHash").value("def456"))
        .andExpect(jsonPath("$.cci").value(0.75));
  }

  @Test
  void getRunReturnsNotFoundWhenMissing() throws Exception {
    when(runIngestionService.getRunSummary(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/v1/runs/999"))
        .andExpect(status().isNotFound());
  }
}
