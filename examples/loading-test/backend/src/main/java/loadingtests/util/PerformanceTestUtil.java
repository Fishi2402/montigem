/* (c) https://github.com/MontiCore/monticore */
package loadingtests.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class PerformanceTestUtil {

  public record TestResult(String testName, int numberOfObjects, int batchSize,
                            double durationInMillis) {
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public static void writeResultsToCsv(String fileName, List<TestResult> results) {
    new File("build").mkdirs();
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write("Number of Objects,Batch Size,Duration (ms)\n");
      for (TestResult result : results) {
        writer.write(String.format(Locale.US, "%d,%d,%.2f\n",
            result.numberOfObjects(),
            result.batchSize(),
            result.durationInMillis()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public static void printPerformanceMatrix(String title, List<TestResult> results) {
    System.out.println("\n--- " + title + " ---");
    Map<Integer, Map<String, Double>> matrix = new TreeMap<>();
    Set<String> allTestNames = new TreeSet<>();
    for (TestResult r : results) {
      matrix.computeIfAbsent(r.numberOfObjects(), k -> new TreeMap<>()).put(r.testName(), r.durationInMillis());
      allTestNames.add(r.testName());
    }

    // Header
    System.out.printf("%-15s", "Objects \\ Test");
    for (String tn : allTestNames) {
      String label = formatLabel(tn);
      System.out.printf(" | %-10s", label);
    }
    System.out.println();

    // Rows
    for (Map.Entry<Integer, Map<String, Double>> entry : matrix.entrySet()) {
      System.out.printf("%-15d", entry.getKey());
      for (String tn : allTestNames) {
        Double val = entry.getValue().get(tn);
        if (val != null) {
          System.out.printf(" | %-10.2f", val);
        } else {
          System.out.printf(" | %-10s", "-");
        }
      }
      System.out.println();
    }
    System.out.println("-------------------------------------------\n");
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public static void writeHtmlReport(String fileName, String title, List<TestResult> results) {
    Map<String, Map<Integer, Double>> data = new TreeMap<>();
    Set<Integer> objectCounts = new TreeSet<>();

    for (TestResult r : results) {
      if (r.testName().startsWith("Warmup")) {
        continue;
      }
      data.computeIfAbsent(r.testName(), k -> new TreeMap<>()).put(r.numberOfObjects(), r.durationInMillis());
      objectCounts.add(r.numberOfObjects());
    }

    StringBuilder html = new StringBuilder();
    html.append("<html><head><title>").append(title).append("</title>");
    html.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
    html.append("<style>body{font-family:sans-serif;padding:20px;background:#f5f5f5;}.chart-container{background:white;padding:20px;border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,0.1);}</style>");
    html.append("</head><body>");
    html.append("<h1>").append(title).append("</h1>");
    html.append("<div class=\"chart-container\"><canvas id=\"perfChart\"></canvas></div>");
    html.append("<script>");
    html.append("const ctx = document.getElementById('perfChart');");
    html.append("new Chart(ctx, { type: 'line', data: {");
    html.append("labels: [").append(objectCounts.stream().map(String::valueOf).collect(Collectors.joining(","))).append("],");
    html.append("datasets: [");

    List<String> datasets = new ArrayList<>();
    int colorIdx = 0;
    String[] colors = {"#4bc0c0", "#36a2eb", "#ff6384", "#ff9f40", "#9966ff", "#ffcd56", "#c9cbcf"};

    for (String tn : data.keySet()) {
      List<String> values = new ArrayList<>();
      for (int oc : objectCounts) {
        Double v = data.get(tn).get(oc);
        values.add(v == null ? "null" : String.format(Locale.US, "%.2f", v));
      }

      String label = formatLabel(tn);
      datasets.add("{ label: '" + label + "', data: [" + String.join(",", values) + "], borderColor: '" + colors[colorIdx % colors.length] + "', fill: false, tension: 0.1 }");
      colorIdx++;
    }

    html.append(String.join(",", datasets));
    html.append("]}, options: { responsive: true, scales: { y: { title: { display: true, text: 'Duration (ms)' }, type: 'logarithmic' }, x: { title: { display: true, text: 'Number of Objects' } } } } });");
    html.append("</script></body></html>");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.write(html.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  private static String formatLabel(String tn) {
    if (tn.startsWith("testLoadAllNormal_run_")) {
      return "Normal (Run " + tn.substring(tn.lastIndexOf("_") + 1) + ")";
    } else if (tn.startsWith("testLoadAllWithIDsBatchSize")) {
      return "Batch Size " + tn.substring("testLoadAllWithIDsBatchSize".length());
    } else if (tn.equals("testLoadAllNormal")) {
      return "Normal (No Batch)";
    }
    return tn;
  }
}
