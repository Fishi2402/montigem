package gui.pipelines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PipelineStatsTest {

  PipelineStats pipelineStats;

  @BeforeEach
  void setUp() {
    pipelineStats = new PipelineStats(null); //"mocking" the project should be fine
  }

  @Test
  void formatDate() {
    LocalDate localDate = LocalDate.of(2024, 2, 23);
    assertEquals("23.02.2024", pipelineStats.formatDate(localDate));
  }
}
