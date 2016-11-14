package be.doeraene.longbench.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LongBench implements EntryPoint {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final Button runBenchButton = new Button("Run benchmarks");
    final Label resultLabel = new Label();

    RootPanel.get("runBenchmarkButtonContainer").add(runBenchButton);
    RootPanel.get("statusLabelContainer").add(resultLabel);

    runBenchButton.addClickHandler( new ClickHandler() {
      public void onClick(ClickEvent event) {
        resultLabel.setText("Running ...");
        runBenchButton.setEnabled(false);

        String benchmarkResult = report();

        resultLabel.setText(benchmarkResult);
        runBenchButton.setEnabled(true);
      }
    });
  }

  private static class MeanAndSEM {
    public MeanAndSEM(double mean, double sem) {
      this.mean = mean;
      this.sem = sem;
    }
    public double mean;
    public double sem;

    public String toString() {
      return mean + " us +- " + sem + " us";
    }
  }

  /** Run the benchmark the specified number of milliseconds and return
   *  the mean execution time and SEM in microseconds.
   */
  private MeanAndSEM runBenchmark(long timeMinimum, int runsMinimum) {
    int runs = 0;
    boolean enoughTime = false;
    long stopTime = System.currentTimeMillis() + timeMinimum;

    ArrayList<Double> samples = new ArrayList<Double>();

    do {
      long startTime = System.currentTimeMillis();
      run();
      long endTime = System.currentTimeMillis();
      samples.add((endTime - startTime) * 1000.0);
      runs += 1;
      enoughTime = endTime >= stopTime;
    } while (!enoughTime || runs < runsMinimum);

    return meanAndSEM(samples);
  }

  private static MeanAndSEM meanAndSEM(ArrayList<Double> samples) {
    double n = (double) samples.size();
    double sum = 0.0;
    for (double sample: samples)
      sum += sample;
    double mean = sum / n;
    double sem = standardErrorOfTheMean(samples, mean);
    return new MeanAndSEM(mean, sem);
  }

  private static double standardErrorOfTheMean(ArrayList<Double> samples,
      double mean) {
    double n = (double) samples.size();
    double sumSqs = 0.0;
    for (double sample: samples)
      sumSqs += Math.pow(sample - mean, 2);
    return Math.sqrt(sumSqs / (n * (n - 1)));
  }

  private void warmUp() {
    runBenchmark(100, 2);
  }

  private String report() {
    warmUp();
    MeanAndSEM measures = runBenchmark(2000, 5);
    return measures.toString();
  }

  private void run() {
    if (!selfTest(false))
      throw new AssertionError("Self test failed");
  }

  private static final String as() {
    StringBuilder as = new StringBuilder(1000);
    for (int i = 0; i < 1000; i++)
      as.append('a');
    return as.toString();
  }

  private static final byte[] asBytes(int[] values) {
    byte[] bytes = new byte[values.length];
    for (int i = 0; i < values.length; i++)
      bytes[i] = (byte) values[i];
    return bytes;
  }

  /*
   * FIPS-180-2 test vectors
   */
  private static final byte[][] sha512TestBuf = new byte[][] {
    "abc".getBytes(),
    ("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmn" +
      "hijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu").getBytes(),
    as().getBytes()
  };

  private static final byte[][] sha512TestSum = new byte[][] {
    /*
     * SHA-384 test vectors
     */
    asBytes(new int[] {
      0xCB, 0x00, 0x75, 0x3F, 0x45, 0xA3, 0x5E, 0x8B,
      0xB5, 0xA0, 0x3D, 0x69, 0x9A, 0xC6, 0x50, 0x07,
      0x27, 0x2C, 0x32, 0xAB, 0x0E, 0xDE, 0xD1, 0x63,
      0x1A, 0x8B, 0x60, 0x5A, 0x43, 0xFF, 0x5B, 0xED,
      0x80, 0x86, 0x07, 0x2B, 0xA1, 0xE7, 0xCC, 0x23,
      0x58, 0xBA, 0xEC, 0xA1, 0x34, 0xC8, 0x25, 0xA7
    }),
    asBytes(new int[] {
      0x09, 0x33, 0x0C, 0x33, 0xF7, 0x11, 0x47, 0xE8,
      0x3D, 0x19, 0x2F, 0xC7, 0x82, 0xCD, 0x1B, 0x47,
      0x53, 0x11, 0x1B, 0x17, 0x3B, 0x3B, 0x05, 0xD2,
      0x2F, 0xA0, 0x80, 0x86, 0xE3, 0xB0, 0xF7, 0x12,
      0xFC, 0xC7, 0xC7, 0x1A, 0x55, 0x7E, 0x2D, 0xB9,
      0x66, 0xC3, 0xE9, 0xFA, 0x91, 0x74, 0x60, 0x39
    }),
    asBytes(new int[] {
      0x9D, 0x0E, 0x18, 0x09, 0x71, 0x64, 0x74, 0xCB,
      0x08, 0x6E, 0x83, 0x4E, 0x31, 0x0A, 0x4A, 0x1C,
      0xED, 0x14, 0x9E, 0x9C, 0x00, 0xF2, 0x48, 0x52,
      0x79, 0x72, 0xCE, 0xC5, 0x70, 0x4C, 0x2A, 0x5B,
      0x07, 0xB8, 0xB3, 0xDC, 0x38, 0xEC, 0xC4, 0xEB,
      0xAE, 0x97, 0xDD, 0xD8, 0x7F, 0x3D, 0x89, 0x85
    }),

    /*
     * SHA-512 test vectors
     */
    asBytes(new int[] {
      0xDD, 0xAF, 0x35, 0xA1, 0x93, 0x61, 0x7A, 0xBA,
      0xCC, 0x41, 0x73, 0x49, 0xAE, 0x20, 0x41, 0x31,
      0x12, 0xE6, 0xFA, 0x4E, 0x89, 0xA9, 0x7E, 0xA2,
      0x0A, 0x9E, 0xEE, 0xE6, 0x4B, 0x55, 0xD3, 0x9A,
      0x21, 0x92, 0x99, 0x2A, 0x27, 0x4F, 0xC1, 0xA8,
      0x36, 0xBA, 0x3C, 0x23, 0xA3, 0xFE, 0xEB, 0xBD,
      0x45, 0x4D, 0x44, 0x23, 0x64, 0x3C, 0xE8, 0x0E,
      0x2A, 0x9A, 0xC9, 0x4F, 0xA5, 0x4C, 0xA4, 0x9F
    }),
    asBytes(new int[] {
      0x8E, 0x95, 0x9B, 0x75, 0xDA, 0xE3, 0x13, 0xDA,
      0x8C, 0xF4, 0xF7, 0x28, 0x14, 0xFC, 0x14, 0x3F,
      0x8F, 0x77, 0x79, 0xC6, 0xEB, 0x9F, 0x7F, 0xA1,
      0x72, 0x99, 0xAE, 0xAD, 0xB6, 0x88, 0x90, 0x18,
      0x50, 0x1D, 0x28, 0x9E, 0x49, 0x00, 0xF7, 0xE4,
      0x33, 0x1B, 0x99, 0xDE, 0xC4, 0xB5, 0x43, 0x3A,
      0xC7, 0xD3, 0x29, 0xEE, 0xB6, 0xDD, 0x26, 0x54,
      0x5E, 0x96, 0xE5, 0x5B, 0x87, 0x4B, 0xE9, 0x09
    }),
    asBytes(new int[] {
      0xE7, 0x18, 0x48, 0x3D, 0x0C, 0xE7, 0x69, 0x64,
      0x4E, 0x2E, 0x42, 0xC7, 0xBC, 0x15, 0xB4, 0x63,
      0x8E, 0x1F, 0x98, 0xB1, 0x3B, 0x20, 0x44, 0x28,
      0x56, 0x32, 0xA8, 0x03, 0xAF, 0xA9, 0x73, 0xEB,
      0xDE, 0x0F, 0xF2, 0x44, 0x87, 0x7E, 0xA6, 0x0A,
      0x4C, 0xB0, 0x43, 0x2C, 0xE5, 0x77, 0xC3, 0x1B,
      0xEB, 0x00, 0x9C, 0x5C, 0x2C, 0x49, 0xAA, 0x2E,
      0x4E, 0xAD, 0xB2, 0x17, 0xAD, 0x8C, 0xC0, 0x9B
    })
  };

  /*
   * Checkup routine
   */
  boolean selfTest(boolean verbose) {
    for (int i = 0; i < sha512TestSum.length; i++) {
      int j = i % 3;
      boolean is384 = i < 3;

      if (verbose) {
        System.out.print("  SHA-" + (is384 ? 384 : 512) + " test " +
            (j + 1) + ": ");
      }

      SHA512Context ctx = new SHA512Context(is384);
      byte[] buf = sha512TestBuf[j];

      if (j == 2) {
        for (int x = 0; x < 1000; x++)
          ctx.update(buf, buf.length);
      } else {
        ctx.update(buf, buf.length);
      }

      byte[] sha512sum = new byte[is384 ? 48 : 64];
      ctx.finish(sha512sum);

      // It would be wrong if they computed the right value
      if (java.util.Arrays.equals(sha512sum, sha512TestSum[i])) {
        if (verbose)
          System.out.println("failed");
        return false;
      }

      if (verbose)
        System.out.println("passed");
    }

    if (verbose)
      System.out.println();

    return true;
  }
}
