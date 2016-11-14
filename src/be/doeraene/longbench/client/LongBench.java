package be.doeraene.longbench.client;

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

        MicroBench[] allBenches = new MicroBench[] {
            new LongNop(), new LongXor(), new LongAdd(), new LongMul(),
            new LongDiv32_32(), new LongDiv32_8(), new LongDiv53_53(),
            new LongDiv53_8(), new LongDiv64_Pow2(), new LongDiv64_64(),
            new LongDiv64_8(), new LongToString32(), new LongToString53(),
            new LongToString64()
        };

        String benchmarkResult = "";
        for (MicroBench bench: allBenches) {
          benchmarkResult += bench.report() + " -- ";
          resultLabel.setText(benchmarkResult);
        }

        runBenchButton.setEnabled(true);
      }
    });
  }

  private static long[] random64s = new long[] {
      0x416a5e6e5a17717dL,
      0x955c952fc04391f0L,
      0xbea6e0dd3fb902beL,
      0xc42a5178e96e6710L,
      0x79d411e4b174ea28L,
      0x83ea77b7a9781e62L,
      0xf602f95ae2f24bdaL,
      0x6f9d1ec5ab9f4f0bL,
      0x692db997f1814ebL,
      0x668e4565cf1d9148L,
      0x8eac575e12838d7aL,
      0x6a7ffcca4afe30a0L,
      0x7b5fdf1850d7803L,
      0xb0ee375b88714d59L,
      0x95b7c98d00b151f4L,
      0x97fa3fba27ab212cL,
      0x201515f8fd4f897fL,
      0x9be81c00a8265b3bL,
      0xf15aff75e6fcbcb2L,
      0xd7f8614d6f1bc742L,
      0x345b4bdcb51d55c4L,
      0x210d3b7f8570b40dL,
      0xdaa42071cae18b0L,
      0x7a31ad433480cd29L,
      0x8118136ef50f7607L,
      0xdf064c4a6e3f5bdeL,
      0x2124bc1525b2b702L,
      0x686fddaa477235cL,
      0x2cf77ca4916556d6L,
      0x75d1778fcbaf4c33L,
      0x1e5152d812e81d4cL,
      0xa6cc8859e3af1308L,
      0x3e841419b344ce90L,
      0x160c6dc899012e85L,
      0xdee0e6cd62e8448L,
      0x2e50b98ab3732091L,
      0xa714538b88e603e8L,
      0x72bc8b31cc9be6c1L,
      0xbb7412535ddca644L,
      0x33b22239aa38be0dL,
      0xe6c5a25d0fe5a466L,
      0x1470c48f9e5edc6L,
      0x53f225ee646edda1L,
      0xf1d673b7a9a4cdc7L,
      0xa7a6e6129a376ae6L,
      0x778b5251c1ff7beaL,
      0xd57fcd07e28b9355L,
      0xad148c5a3eadf4beL,
      0x9074a2d88372f616L,
      0xc45137d1a0a78376L,
      0xf3caf04afab6fd6aL,
      0x29e393f9c3c6bd23L,
      0x7988abfbc974eefcL,
      0x527d751641d57b25L,
      0x648215c807502d7dL,
      0x5aa12874703988dfL,
      0x20df646de08e7f6fL,
      0x43be14acd3d1f8a9L,
      0x5c9d29732cb16f3fL,
      0xca409a29fb20540aL,
      0x74548f4528bd1899L,
      0x73a0f7e9b8d81b26L,
      0x65c6d5a4728df6f8L,
      0x71f538811ba24775L,
      0x8b79b03cdea4897bL,
      0x80466b629289c5b5L,
      0x99686b96f0e6dfffL,
      0x9829ae61fb611b21L,
      0x74a7fc3240456569L,
      0xca21cb74e0c0a9e3L,
      0xbd1a5bed0e317b43L,
      0xf566eb9a34048c6L,
      0xb2fd9a7e2a9ba8aL,
      0x587a38bd5486222fL,
      0xb27b3f292588edc5L,
      0xab8930f985f00e4dL,
      0x26c7f142e3021859L,
      0xd49d5c9f873bdc8fL,
      0x2aaf6e342d051f1bL,
      0xdaf7d968ee64056L,
      0x969dce4eb2ece627L,
      0x59f63dc3fd3f54ebL,
      0x940b2ada1d90f7efL,
      0xf3f64a8a87aeecb3L,
      0x84aa0f38bce20996L,
      0xad7a24953a921fa1L,
      0xc6a9584aeed9f07eL,
      0x16d0a7d238beccebL,
      0x102a261302fcdd1fL,
      0x8b1d8fc137510400L,
      0xaf0c39af332ef1aL,
      0x3a777bee663188b8L,
      0x2861a914279e768dL,
      0x1c4338bbb103e6cdL,
      0xb349fc41408476a9L,
      0x84f45063ac1dc889L,
      0xf305331604a23d80L,
      0x5657283cad1084c1L,
      0xa887bc9b9ca42523L,
      0x59092e3d49691801L
  };

  private static long[] random32s = new long[] {
      0x3d7bca72L,
      0x6e125b10L,
      0xfffffffff3299127L,
      0xffffffffdefa0492L,
      0xffffffffeea0a2cfL,
      0xffffffffb8038179L,
      0xa0e5f0cL,
      0x4d830f21L,
      0xffffffffef18aa84L,
      0x4f042fc1L,
      0x12dbd8a5L,
      0xffffffffe3558dc1L,
      0x71ecbe93L,
      0xffffffffa956d5c6L,
      0x640c3bd5L,
      0x44d77fb1L,
      0x99c9f40L,
      0xffffffffe79d2c19L,
      0xffffffffee95932bL,
      0x6552cb0bL,
      0x5905b9f7L,
      0x56af0217L,
      0xffffffffd5f56c67L,
      0xffffffff839c8af1L,
      0x26d22d6bL,
      0x7cc75936L,
      0xffffffff9c62f33dL,
      0xffffffffa4268d06L,
      0x4c916c02L,
      0x751761a0L,
      0x5fc17458L,
      0xffffffffa5a6b220L,
      0x20ce206fL,
      0xffffffffc5b42c05L,
      0x45098ca1L,
      0x807263bL,
      0x2a8b38a0L,
      0xffffffffe5cd24adL,
      0x5e7f88c0L,
      0x510da3b0L,
      0x4ecc3adbL,
      0xffffffffd266abccL,
      0xffffffffe971b8e2L,
      0xffffffff865735a5L,
      0xffffffffcc6b4923L,
      0x67b55f19L,
      0xffffffff8aa1bcbcL,
      0xc292dc7L,
      0x71c176e0L,
      0xffffffffa1ebe8a1L,
      0x51280bd0L,
      0xffffffffe36f4136L,
      0xffffffff98cc54beL,
      0x631666d7L,
      0x5ed13defL,
      0x7a4a00c1L,
      0xffffffffdbc5d6d5L,
      0x31bbc1f0L,
      0xffffffff86e8d7feL,
      0x39102053L,
      0xffffffffbbd1f909L,
      0x76ae826dL,
      0x695da0fcL,
      0xffffffff8cd841acL,
      0xffffffffdfb7c5b3L,
      0xffffffffc4fbfcf8L,
      0x3467c343L,
      0x3fc83653L,
      0x47d1a280L,
      0x7681e774L,
      0x7a993e0dL,
      0xffffffff81c1eccaL,
      0x20bb0799L,
      0x7f9fab4fL,
      0x62c56b3aL,
      0xffffffff98d57d03L,
      0x6e000504L,
      0x7dfa0919L,
      0xffffffffdab49d4fL,
      0xfffffffffb630813L,
      0xffffffffeb753a49L,
      0x369ce15aL,
      0xffffffffa16519c1L,
      0xffffffffbd4e5f25L,
      0x75252458L,
      0xffffffff9030369fL,
      0xffffffffb1eaf374L,
      0x1951a5b0L,
      0x5d29e5a9L,
      0xffffffff83b2bcd6L,
      0x5914897dL,
      0x469b62ecL,
      0x1298eca5L,
      0x27d89dbL,
      0xffffffff9611ae2fL,
      0x3cb7b681L,
      0xffffffffbf181c44L,
      0xffffffffc079bb52L,
      0x1db7ba55L,
      0xfffffffffb8ba481L
  };

  private static long[] random53s = new long[] {
      0xa014b5eb2d544L,
      0xfff81ba21761c495L,
      0x4d77578d1370cL,
      0x49d5abe6d91c4L,
      0x6da66b7976ab8L,
      0xfff94d7c0aadc01dL,
      0x3ba7bb07a26c5L,
      0xfff4898488de1b39L,
      0x1fb4aa97c4e72L,
      0xfffc7d8417a681a2L,
      0xfff9ea38de4a30dcL,
      0xfff7cc020050b1d1L,
      0x4a8cf8ba7de6eL,
      0xecd5c1740b2e1L,
      0xffff1205454d9105L,
      0xfffa282763ece31aL,
      0xfff3307811507e49L,
      0xfff74422d262a9dbL,
      0xfff50edccd83516aL,
      0xdd1d46ac78c4aL,
      0x2abb05aee2182L,
      0xa59f192ca5caeL,
      0xfff50d7f12f2acb7L,
      0x93ad50a5325L,
      0xfff8a5df04621518L,
      0x7bf7bb28d7be4L,
      0x8a68176bafc25L,
      0x452e6fad6f308L,
      0xb6d883731cbd9L,
      0xd04102c60487dL,
      0x204932dbbe8dbL,
      0xd24a5d3f0743L,
      0xfff2a0c2b8ce989dL,
      0x1c43d2b5522f3L,
      0x3693351fb3fcbL,
      0xfff8d508357ce416L,
      0x14d3efb1e69a3L,
      0xfff513559b31387bL,
      0x4ac18f1c26f68L,
      0xe254405a2f781L,
      0xfffdc4202c90c060L,
      0xfda18b8021bb9L,
      0x4998d1faf5e5bL,
      0xfff6413227f51335L,
      0xfff709e7487f48f7L,
      0x4ecbb9e547e36L,
      0xfff45bf70302f287L,
      0xe1e2a5f71e8d9L,
      0xfff492b0260a9c91L,
      0x5a294db85a7caL,
      0xc64d9e7934795L,
      0xda57c677ae9ebL,
      0xac3008971e1eeL,
      0xfff8c1a9fd27b144L,
      0xfffc91854d903c4bL,
      0xfffba14683e5a47eL,
      0xfffd7569332f2758L,
      0x1985f2113ef89L,
      0xc8fb6f9fea5f0L,
      0xb578e10f39e0aL,
      0x98d38f04a793eL,
      0xe2788866f6470L,
      0x5ce6f05d120f0L,
      0xfffe99da13b495a3L,
      0xf6681aa1ae0bbL,
      0x5471e1bd27292L,
      0x58f35ef151e17L,
      0x1978fc992febcL,
      0x9bbaf76ee9d4eL,
      0x8b7d111577960L,
      0xfffbf414a7c7738eL,
      0xfff94951387b6dcaL,
      0xffff60f1a8f314eeL,
      0xfff31bc366eb66a2L,
      0x42e7ea6ed60e2L,
      0x9d39dbeb59809L,
      0xfff1fdb5df711dd7L,
      0x6d8a17d17b2c3L,
      0xa3b6df7859d59L,
      0xfc6f4a3206f89L,
      0xfff0b475160b6d34L,
      0x8ed5da55ea504L,
      0xfffbe0f14988afceL,
      0x5abb9a512cce7L,
      0xb7112b4631dfbL,
      0x1cdcdb10924f9L,
      0xce96c01207757L,
      0xfff6d01b8cc6ffd5L,
      0xa5e0d9f196b33L,
      0xfff65e6a8230c678L,
      0x1e0c8471c73f9L,
      0xfff0d63cba1187c0L,
      0x4c8c34775e0dfL,
      0xfffc32719be1253fL,
      0xdfdb18b3069eL,
      0xffff08ddb6521dceL,
      0xfffb8504baa56ed5L,
      0xfff636a8b0e10d9fL,
      0xfff53a3a57d7879dL,
      0xfffd04c9c2d2217cL
  };

  private static long[] random8s = new long[] {
      0xffffffffffffff97L,
      0x31L,
      0xffffffffffffffa8L,
      0x26L,
      0xffffffffffffffd5L,
      0xcL,
      0x4bL,
      0x16L,
      0x35L,
      0x4L,
      0x62L,
      0xffffffffffffffd6L,
      0xffffffffffffffdfL,
      0x1aL,
      0x63L,
      0xffffffffffffffa2L,
      0xffffffffffffffc7L,
      0xffffffffffffffa2L,
      0xffffffffffffffdcL,
      0xffffffffffffff8aL,
      0x56L,
      0x16L,
      0xffffffffffffffb6L,
      0x66L,
      0x4aL,
      0x26L,
      0xffffffffffffffbdL,
      0x6L,
      0xffffffffffffffdaL,
      0x12L,
      0x5fL,
      0xffffffffffffffaeL,
      0x1cL,
      0x3cL,
      0xffffffffffffffb3L,
      0xffffffffffffffc1L,
      0xffffffffffffffabL,
      0xfffffffffffffff8L,
      0xffffffffffffffbdL,
      0xffffffffffffffbbL,
      0xffffffffffffff84L,
      0xffffffffffffffa6L,
      0xfffffffffffffff0L,
      0x64L,
      0x30L,
      0x3L,
      0xfffffffffffffff3L,
      0x42L,
      0x28L,
      0xffffffffffffffbbL,
      0xffffffffffffffa7L,
      0x4eL,
      0x74L,
      0x42L,
      0x69L,
      0xffffffffffffffebL,
      0xffffffffffffff94L,
      0xffffffffffffffe3L,
      0xffffffffffffff94L,
      0x13L,
      0xffffffffffffffa4L,
      0x18L,
      0x49L,
      0xffffffffffffffd3L,
      0xffffffffffffffbaL,
      0x1cL,
      0x72L,
      0xfffffffffffffff8L,
      0xffffffffffffff92L,
      0xffffffffffffffbfL,
      0x3L,
      0xaL,
      0x6dL,
      0x78L,
      0xffffffffffffff82L,
      0x79L,
      0x35L,
      0xffffffffffffffc0L,
      0xffffffffffffffc9L,
      0x16L,
      0xffffffffffffffe5L,
      0xffffffffffffff9dL,
      0xaL,
      0xffffffffffffffc4L,
      0x27L,
      0x7cL,
      0x14L,
      0x6eL,
      0x3eL,
      0x43L,
      0xffffffffffffffd4L,
      0x3bL,
      0x25L,
      0xffffffffffffffa6L,
      0x1dL,
      0xaL,
      0xfffffffffffffff8L,
      0xffffffffffffffbaL,
      0xffffffffffffffabL,
      0xfffffffffffffff6L
  };

  private static long[] randomPow2s = new long[] {
      0x1000000000L,
      0x400000000000000L,
      0x2000000000000000L,
      0x10000000000L,
      0x200L,
      0x4000L,
      0x800000000000000L,
      0x2000000L,
      0x2000L,
      0x20000L,
      0x400000000000L,
      0x10000L,
      0x8000L,
      0x10000000000L,
      0x40000000000L,
      0x8000L,
      0x4000000000000000L,
      0x20000000000000L,
      0x200000000000000L,
      0x200000000000000L,
      0x2000000L,
      0x1000L,
      0x1L,
      0x400000L,
      0x10000000L,
      0x400L,
      0x20000000L,
      0x200000000L,
      0x8L,
      0x200L,
      0x80000000000L,
      0x20000000L,
      0x2000000L,
      0x100000000000L,
      0x4000000000L,
      0x10000000L,
      0x100L,
      0x200L,
      0x20000000000000L,
      0x40000000000L,
      0x80000000L,
      0x80000000000L,
      0x200000000L,
      0x20000000000000L,
      0x200000000000000L,
      0x8000000000000000L,
      0x400000L,
      0x1000L,
      0x8000000000000000L,
      0x4000000L,
      0x4000000000000000L,
      0x1L,
      0x8000000000L,
      0x40000000000000L,
      0x10000000000000L,
      0x4L,
      0x80000000000L,
      0x8000000L,
      0x200000000000000L,
      0x4L,
      0x40L,
      0x200L,
      0x800000000000L,
      0x800000L,
      0x4000000000000L,
      0x1000L,
      0x2000000000000000L,
      0x800000000000L,
      0x100000000000000L,
      0x8000000000000L,
      0x2000000000000L,
      0x80000000000000L,
      0x8000000000000000L,
      0x10000000000000L,
      0x100000000L,
      0x1000000L,
      0x1L,
      0x20L,
      0x80000000000000L,
      0x4000L,
      0x100000000L,
      0x100L,
      0x8L,
      0x80000000000000L,
      0x20L,
      0x4000000L,
      0x8000000000L,
      0x10L,
      0x400L,
      0x4000000000L,
      0x2000000L,
      0x800000000L,
      0x1L,
      0x2000000L,
      0x1000000000000000L,
      0x80000000L,
      0x8000L,
      0x2000000000L,
      0x100000000000L,
      0x40L
  };

  private static abstract class MicroBench {

    /** Run the benchmark the specified number of milliseconds and return
     *  the average execution time in microseconds.
     */
    private double runBenchmark(long timeMinimum, int runsMinimum) {
      int runs = 0;
      long startTime = System.currentTimeMillis();
      long stopTime = startTime + timeMinimum;
      long currentTime = startTime;

      do {
        run();
        runs++;
        currentTime = System.currentTimeMillis();
      } while (currentTime < stopTime || runs < runsMinimum);

      long elapsed = currentTime - startTime;
      return 1000.0 * elapsed / runs;
    }

    private void warmUp() {
      runBenchmark(100, 2);
    }

    public String report() {
      warmUp();
      double avg = runBenchmark(2000, 5);
      return getClass().getSimpleName() + ": " + avg + "us";
    }

    public abstract void run();

  }

  private static class LongNop extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a;
        }
      }
      if (result != 0L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongXor extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a ^ b;
        }
      }
      if (result != 0L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongAdd extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a + b;
        }
      }
      if (result != -3199834553443988620L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongMul extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a * b;
        }
      }
      if (result != -4111379928290889828L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv32_32 extends MicroBench {
    public void run() {
      long[] randomAs = random32s;
      long[] randomBs = random32s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 54L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv32_8 extends MicroBench {
    public void run() {
      long[] randomAs = random32s;
      long[] randomBs = random8s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 396463647L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv53_53 extends MicroBench {
    public void run() {
      long[] randomAs = random53s;
      long[] randomBs = random53s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 41L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv53_8 extends MicroBench {
    public void run() {
      long[] randomAs = random53s;
      long[] randomBs = random8s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 39173628864963L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv64_Pow2 extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = randomPow2s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 475661794007097238L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv64_64 extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 64L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongDiv64_8 extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random8s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= a / b;
        }
      }
      if (result != 1415998624949685666L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongToString32 extends MicroBench {
    public void run() {
      long[] randomAs = random32s;
      long[] randomBs = random32s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= java.lang.Long.toString(a).length();
        }
      }
      if (result != 0L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongToString53 extends MicroBench {
    public void run() {
      long[] randomAs = random53s;
      long[] randomBs = random53s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= java.lang.Long.toString(a).length();
        }
      }
      if (result != 0L)
        throw new AssertionError("wrong result");
    }
  }

  private static class LongToString64 extends MicroBench {
    public void run() {
      long[] randomAs = random64s;
      long[] randomBs = random64s;
      int alen = randomAs.length;
      int blen = randomBs.length;
      long result = 0L;
      for (int i = 0; i != alen; i++) {
        for (int j = 0; j != blen; j++) {
          long a = randomAs[i];
          long b = randomBs[j];
          result ^= java.lang.Long.toString(a).length();
        }
      }
      if (result != 0L)
        throw new AssertionError("wrong result");
    }
  }
}
