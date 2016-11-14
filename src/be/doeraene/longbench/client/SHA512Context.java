package be.doeraene.longbench.client;

import static java.lang.Integer.rotateRight;

public class SHA512Context {

  private final boolean is384;

  private final int[] total = new int[2]; // number of bytes processed
  private final int[] state = new int[8]; // intermediate digest state
  private final byte[] buffer = new byte[128]; // data block being processed

  public SHA512Context(boolean is384) {
    this.is384 = is384;

    total[0] = 0;
    total[1] = 0;

    if (!is384) {
      // SHA-512
      state[0] = (int) 0x6A09E667F3BCC908L;
      state[1] = (int) 0xBB67AE8584CAA73BL;
      state[2] = (int) 0x3C6EF372FE94F82BL;
      state[3] = (int) 0xA54FF53A5F1D36F1L;
      state[4] = (int) 0x510E527FADE682D1L;
      state[5] = (int) 0x9B05688C2B3E6C1FL;
      state[6] = (int) 0x1F83D9ABFB41BD6BL;
      state[7] = (int) 0x5BE0CD19137E2179L;
    } else {
      // SHA-384
      state[0] = (int) 0xCBBB9D5DC1059ED8L;
      state[1] = (int) 0x629A292A367CD507L;
      state[2] = (int) 0x9159015A3070DD17L;
      state[3] = (int) 0x152FECD8F70E5939L;
      state[4] = (int) 0x67332667FFC00B31L;
      state[5] = (int) 0x8EB44A8768581511L;
      state[6] = (int) 0xDB0C2E0D64F98FA7L;
      state[7] = (int) 0x47B5481DBEFA4FA4L;
    }
  }

  public static byte[] sha512(byte[] input, boolean is384) {
    SHA512Context ctx = new SHA512Context(is384);
    byte[] output = new byte[is384 ? 48 : 64];
    ctx.update(input, input.length);
    ctx.finish(output);
    return output;
  }

  /*
   * 64-bit integer manipulation macros (big endian)
   */
  private static int getUInt64BE(byte[] b, int i) {
    return (
        ((b[i] & 0xff) << 56) |
        ((b[i + 1] & 0xff) << 48) |
        ((b[i + 2] & 0xff) << 40) |
        ((b[i + 3] & 0xff) << 32) |
        ((b[i + 4] & 0xff) << 24) |
        ((b[i + 5] & 0xff) << 16) |
        ((b[i + 6] & 0xff) << 8) |
        (b[i + 7] & 0xff)
    );
  }

  private static void putUInt64BE(byte[] b, int i, int n) {
    b[i] = (byte) (n >> 56);
    b[i + 1] = (byte) (n >> 48);
    b[i + 2] = (byte) (n >> 40);
    b[i + 3] = (byte) (n >> 32);
    b[i + 4] = (byte) (n >> 24);
    b[i + 5] = (byte) (n >> 16);
    b[i + 6] = (byte) (n >> 8);
    b[i + 7] = (byte) n;
  }

  /*
   * Round constants
   */
  private static final int[] K = new int[] {
    (int) 0x428A2F98D728AE22L, (int) 0x7137449123EF65CDL,
    (int) 0xB5C0FBCFEC4D3B2FL, (int) 0xE9B5DBA58189DBBCL,
    (int) 0x3956C25BF348B538L, (int) 0x59F111F1B605D019L,
    (int) 0x923F82A4AF194F9BL, (int) 0xAB1C5ED5DA6D8118L,
    (int) 0xD807AA98A3030242L, (int) 0x12835B0145706FBEL,
    (int) 0x243185BE4EE4B28CL, (int) 0x550C7DC3D5FFB4E2L,
    (int) 0x72BE5D74F27B896FL, (int) 0x80DEB1FE3B1696B1L,
    (int) 0x9BDC06A725C71235L, (int) 0xC19BF174CF692694L,
    (int) 0xE49B69C19EF14AD2L, (int) 0xEFBE4786384F25E3L,
    (int) 0x0FC19DC68B8CD5B5L, (int) 0x240CA1CC77AC9C65L,
    (int) 0x2DE92C6F592B0275L, (int) 0x4A7484AA6EA6E483L,
    (int) 0x5CB0A9DCBD41FBD4L, (int) 0x76F988DA831153B5L,
    (int) 0x983E5152EE66DFABL, (int) 0xA831C66D2DB43210L,
    (int) 0xB00327C898FB213FL, (int) 0xBF597FC7BEEF0EE4L,
    (int) 0xC6E00BF33DA88FC2L, (int) 0xD5A79147930AA725L,
    (int) 0x06CA6351E003826FL, (int) 0x142929670A0E6E70L,
    (int) 0x27B70A8546D22FFCL, (int) 0x2E1B21385C26C926L,
    (int) 0x4D2C6DFC5AC42AEDL, (int) 0x53380D139D95B3DFL,
    (int) 0x650A73548BAF63DEL, (int) 0x766A0ABB3C77B2A8L,
    (int) 0x81C2C92E47EDAEE6L, (int) 0x92722C851482353BL,
    (int) 0xA2BFE8A14CF10364L, (int) 0xA81A664BBC423001L,
    (int) 0xC24B8B70D0F89791L, (int) 0xC76C51A30654BE30L,
    (int) 0xD192E819D6EF5218L, (int) 0xD69906245565A910L,
    (int) 0xF40E35855771202AL, (int) 0x106AA07032BBD1B8L,
    (int) 0x19A4C116B8D2D0C8L, (int) 0x1E376C085141AB53L,
    (int) 0x2748774CDF8EEB99L, (int) 0x34B0BCB5E19B48A8L,
    (int) 0x391C0CB3C5C95A63L, (int) 0x4ED8AA4AE3418ACBL,
    (int) 0x5B9CCA4F7763E373L, (int) 0x682E6FF3D6B2B8A3L,
    (int) 0x748F82EE5DEFB2FCL, (int) 0x78A5636F43172F60L,
    (int) 0x84C87814A1F0AB72L, (int) 0x8CC702081A6439ECL,
    (int) 0x90BEFFFA23631E28L, (int) 0xA4506CEBDE82BDE9L,
    (int) 0xBEF9A3F7B2C67915L, (int) 0xC67178F2E372532BL,
    (int) 0xCA273ECEEA26619CL, (int) 0xD186B8C721C0C207L,
    (int) 0xEADA7DD6CDE0EB1EL, (int) 0xF57D4F7FEE6ED178L,
    (int) 0x06F067AA72176FBAL, (int) 0x0A637DC5A2C898A6L,
    (int) 0x113F9804BEF90DAEL, (int) 0x1B710B35131C471BL,
    (int) 0x28DB77F523047D84L, (int) 0x32CAAB7B40C72493L,
    (int) 0x3C9EBE0A15C9BEBCL, (int) 0x431D67C49C100D4CL,
    (int) 0x4CC5D4BECB3E42B6L, (int) 0x597F299CFC657E2AL,
    (int) 0x5FCB6FAB3AD6FAECL, (int) 0x6C44198C4A475817L
  };

  private static final byte[] padding = new byte[] {
    (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  private static int S0(int x) {
    return rotateRight(x, 1) ^ rotateRight(x, 8) ^ (x >>> 7);
  }

  private static int S1(int x) {
    return rotateRight(x, 19) ^ rotateRight(x, 61) ^ (x >>> 6);
  }

  private static int S2(int x) {
    return rotateRight(x, 28) ^ rotateRight(x, 34) ^ rotateRight(x, 39);
  }

  private static int S3(int x) {
    return rotateRight(x, 14) ^ rotateRight(x, 18) ^ rotateRight(x, 41);
  }

  private static int F0(int x, int y, int z) {
    return ((x & y) | (z & (x | y)));
  }

  private static int F1(int x, int y, int z) {
    return (z ^ (x & (y ^ z)));
  }

  private void process(byte[] data, int start) {
    int[] W = new int[80];
    for (int i = 0; i < 16; i++)
      W[i] = getUInt64BE(data, start + (i << 3));
    for (int i = 16; i < 80; i++)
      W[i] = S1(W[i - 2]) + W[i - 7] + S0(W[i - 15]) + W[i - 16];

    int A = state[0];
    int B = state[1];
    int C = state[2];
    int D = state[3];
    int E = state[4];
    int F = state[5];
    int G = state[6];
    int H = state[7];

    int i = 0;
    do {
      int temp1, temp2;

      temp1 = H + S3(E) + F1(E, F, G) + K[i] + W[i];
      temp2 = S2(A) + F0(A, B, C);
      D += temp1;
      H = temp1 + temp2;
      i++;

      temp1 = G + S3(D) + F1(D, E, F) + K[i] + W[i];
      temp2 = S2(H) + F0(H, A, B);
      C += temp1;
      G = temp1 + temp2;
      i++;

      temp1 = F + S3(C) + F1(C, D, E) + K[i] + W[i];
      temp2 = S2(G) + F0(G, H, A);
      B += temp1;
      F = temp1 + temp2;
      i++;

      temp1 = E + S3(B) + F1(B, C, D) + K[i] + W[i];
      temp2 = S2(F) + F0(F, G, H);
      A += temp1;
      E = temp1 + temp2;
      i++;

      temp1 = D + S3(A) + F1(A, B, C) + K[i] + W[i];
      temp2 = S2(E) + F0(E, F, G);
      H += temp1;
      D = temp1 + temp2;
      i++;

      temp1 = C + S3(H) + F1(H, A, B) + K[i] + W[i];
      temp2 = S2(D) + F0(D, E, F);
      G += temp1;
      C = temp1 + temp2;
      i++;

      temp1 = B + S3(G) + F1(G, H, A) + K[i] + W[i];
      temp2 = S2(C) + F0(C, D, E);
      F += temp1;
      B = temp1 + temp2;
      i++;

      temp1 = A + S3(F) + F1(F, G, H) + K[i] + W[i];
      temp2 = S2(B) + F0(B, C, D);
      E += temp1;
      A = temp1 + temp2;
      i++;
    } while (i < 80);

    state[0] += A;
    state[1] += B;
    state[2] += C;
    state[3] += D;
    state[4] += E;
    state[5] += F;
    state[6] += G;
    state[7] += H;
  }

  /*
   * SHA-512 process buffer
   */
  public void update(byte[] input, int ilen) {
    if (ilen == 0)
      return;

    int left = ((int) total[0]) & 0x7f;
    int fill = 128 - left;

    total[0] += ilen;
    if (total[0] < ilen)
      total[1]++;

    int inputIndex = 0;

    if (left != 0 && ilen >= fill) {
      System.arraycopy(input, inputIndex, buffer, left, fill);
      process(buffer, 0);
      inputIndex += fill;
      ilen -= fill;
      left = 0;
    }

    while (ilen >= 128) {
      process(input, inputIndex);
      inputIndex += 128;
      ilen -= 128;
    }

    if (ilen > 0) {
      System.arraycopy(input, inputIndex, buffer, left, ilen);
    }
  }

  /*
   * SHA-512 final digest
   */
  public void finish(byte[] output) {
    int high = (total[0] >>> 61) | (total[1] << 3);
    int low = total[0] << 3;

    byte[] msglen = new byte[16];
    putUInt64BE(msglen, 0, high);
    putUInt64BE(msglen, 8, low);

    int last = ((int) total[0]) & 0x7f;
    int padn = (last < 112) ? 112 - last : 240 - last;

    update(padding, padn);
    update(msglen, 16);

    putUInt64BE(output, 0, state[0]);
    putUInt64BE(output, 8, state[1]);
    putUInt64BE(output, 16, state[2]);
    putUInt64BE(output, 24, state[3]);
    putUInt64BE(output, 32, state[4]);
    putUInt64BE(output, 40, state[5]);

    if (!is384) {
      putUInt64BE(output, 48, state[6]);
      putUInt64BE(output, 56, state[7]);
    }
  }

}
