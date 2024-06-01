package gui.pipelines;

import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import mc.fenix.charts.GemBarChartTypes.GemBarChartData;
import mc.fenix.charts.GemBarChartTypes.GemBarChartEntry;
import org.junit.jupiter.api.Test;
import umlp.backendrte.test_infrastructure.ClientSetup;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PipelineStatsE2ETest extends DtcockpitMockTestSetup {

  public PipelineStatsE2ETest() {
    super(ClientSetup.of(1));
  }

  @Test
  public void testGemCardTitle() {
    // Create dummy data
    long id = 123;
    String projectName = "SomeProject";
    DtcockpitManager
        .projectBuilderFromId(id)
        .name(projectName)
        .git("http://example.com/git.git")
        .build();

    getClients(0).activate();
    dtcockpit.Project projectInClient = DtcockpitManager.getProjectSure(id);

    PipelineStats pageUnderTest = new PipelineStats(projectInClient);

    assertEquals("Pipeline dashboard for " + projectName, pageUnderTest.getGemCard0Title());
  }

  @Test
  public void testBarChartForPipelineRunsData() {
    //create project
    long id = 123;
    String projectName = "SomeProject";
    Optional<Project> buildProject = DtcockpitManager
        .projectBuilderFromId(id)
        .name(projectName)
        .git("http://example.com/git.git")
        .build();
    assertTrue(buildProject.isPresent());
    Project projectInBackend = buildProject.get();

    //add p stats
    LocalDate date1 = LocalDate.of(2024, 2, 23);
    String date1Str = "23.02.2024";
    LocalDate date2 = LocalDate.of(2024, 2, 24);
    String date2Str = "24.02.2024";
    LocalDate date3 = LocalDate.of(2024, 2, 26);
    String date3Str = "26.02.2024";

    DtcockpitManager
        .pipelineStatisticBuilder()
        .project(projectInBackend)
        .duration(3L)
        .successful(true)
        .time(date1)
        .build();
    DtcockpitManager
        .pipelineStatisticBuilder()
        .project(projectInBackend)
        .duration(4L)
        .successful(true)
        .time(date1)
        .build();
    DtcockpitManager
        .pipelineStatisticBuilder()
        .project(projectInBackend)
        .duration(2L)
        .successful(false)
        .time(date1)
        .build();
    DtcockpitManager
        .pipelineStatisticBuilder()
        .project(projectInBackend)
        .duration(3L)
        .successful(true)
        .time(date2)
        .build();
    DtcockpitManager
        .pipelineStatisticBuilder()
        .project(projectInBackend)
        .duration(2L)
        .successful(false)
        .time(date3)
        .build();

    getClients(0).activate();
    Project projectInClient = DtcockpitManager.getProjectSure(id);

    PipelineStats pageUnderTest = new PipelineStats(projectInClient);
    GemBarChartData data = pageUnderTest.getBarChartForPipelineRunsData();

    assertEquals(List.of(date1Str, date2Str, date3Str), data.getLabels());

    List<GemBarChartEntry> entries = data.getEntries();
    assertEquals(2, entries.size());

    GemBarChartEntry entry1 = entries.get(0);
    Optional<String> optLabel1 = entry1.getLabel();
    assertTrue(optLabel1.isPresent());
    assertEquals("Unsuccessful", optLabel1.get());
    assertTrue(entry1.getStack().isEmpty());
    assertEquals(List.of(1, 0, 1), entry1.getData());
    Optional<String> optBackgroundColor1 = entry1.getBackgroundColor();
    assertTrue(optBackgroundColor1.isPresent());
    assertEquals("#cc071e", optBackgroundColor1.get());
    assertTrue(entry1.getHoverBackgroundColor().isEmpty());

    GemBarChartEntry entry2 = entries.get(1);
    Optional<String> optLabel2 = entry2.getLabel();
    assertTrue(optLabel2.isPresent());
    assertEquals("Successful", optLabel2.get());
    assertTrue(entry2.getStack().isEmpty());
    assertEquals(List.of(2, 1, 0), entry2.getData());
    Optional<String> optBackgroundColor2 = entry2.getBackgroundColor();
    assertTrue(optBackgroundColor2.isPresent());
    assertEquals("#57ab27", optBackgroundColor2.get());
    assertTrue(entry2.getHoverBackgroundColor().isEmpty());
  }
}
