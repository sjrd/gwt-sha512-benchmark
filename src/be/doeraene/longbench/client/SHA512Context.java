package be.doeraene.longbench.client;

import static java.lang.Long.rotateRight;

public class SHA512Context {

  private final boolean is384;

  private final long[] total = new long[2]; // number of bytes processed
  private final long[] state = new long[8]; // intermediate digest state
  private final byte[] buffer = new byte[128]; // data block being processed

  public SHA512Context(boolean is384) {
    this.is384 = is384;

    total[0] = 0;
    total[1] = 0;

    if (!is384) {
      // SHA-512
      state[0] = 0x6A09E667F3BCC908L;
      state[1] = 0xBB67AE8584CAA73BL;
      state[2] = 0x3C6EF372FE94F82BL;
      state[3] = 0xA54FF53A5F1D36F1L;
      state[4] = 0x510E527FADE682D1L;
      state[5] = 0x9B05688C2B3E6C1FL;
      state[6] = 0x1F83D9ABFB41BD6BL;
      state[7] = 0x5BE0CD19137E2179L;
    } else {
      // SHA-384
      state[0] = 0xCBBB9D5DC1059ED8L;
      state[1] = 0x629A292A367CD507L;
      state[2] = 0x9159015A3070DD17L;
      state[3] = 0x152FECD8F70E5939L;
      state[4] = 0x67332667FFC00B31L;
      state[5] = 0x8EB44A8768581511L;
      state[6] = 0xDB0C2E0D64F98FA7L;
      state[7] = 0x47B5481DBEFA4FA4L;
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
  private static long getUInt64BE(byte[] b, int i) {
    return (
        ((b[i] & 0xffL) << 56) |
        ((b[i + 1] & 0xffL) << 48) |
        ((b[i + 2] & 0xffL) << 40) |
        ((b[i + 3] & 0xffL) << 32) |
        ((b[i + 4] & 0xffL) << 24) |
        ((b[i + 5] & 0xffL) << 16) |
        ((b[i + 6] & 0xffL) << 8) |
        (b[i + 7] & 0xffL)
    );
  }

  private static void putUInt64BE(byte[] b, int i, long n) {
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
  private static final long[] K = new long[] {
    0x428A2F98D728AE22L, 0x7137449123EF65CDL,
    0xB5C0FBCFEC4D3B2FL, 0xE9B5DBA58189DBBCL,
    0x3956C25BF348B538L, 0x59F111F1B605D019L,
    0x923F82A4AF194F9BL, 0xAB1C5ED5DA6D8118L,
    0xD807AA98A3030242L, 0x12835B0145706FBEL,
    0x243185BE4EE4B28CL, 0x550C7DC3D5FFB4E2L,
    0x72BE5D74F27B896FL, 0x80DEB1FE3B1696B1L,
    0x9BDC06A725C71235L, 0xC19BF174CF692694L,
    0xE49B69C19EF14AD2L, 0xEFBE4786384F25E3L,
    0x0FC19DC68B8CD5B5L, 0x240CA1CC77AC9C65L,
    0x2DE92C6F592B0275L, 0x4A7484AA6EA6E483L,
    0x5CB0A9DCBD41FBD4L, 0x76F988DA831153B5L,
    0x983E5152EE66DFABL, 0xA831C66D2DB43210L,
    0xB00327C898FB213FL, 0xBF597FC7BEEF0EE4L,
    0xC6E00BF33DA88FC2L, 0xD5A79147930AA725L,
    0x06CA6351E003826FL, 0x142929670A0E6E70L,
    0x27B70A8546D22FFCL, 0x2E1B21385C26C926L,
    0x4D2C6DFC5AC42AEDL, 0x53380D139D95B3DFL,
    0x650A73548BAF63DEL, 0x766A0ABB3C77B2A8L,
    0x81C2C92E47EDAEE6L, 0x92722C851482353BL,
    0xA2BFE8A14CF10364L, 0xA81A664BBC423001L,
    0xC24B8B70D0F89791L, 0xC76C51A30654BE30L,
    0xD192E819D6EF5218L, 0xD69906245565A910L,
    0xF40E35855771202AL, 0x106AA07032BBD1B8L,
    0x19A4C116B8D2D0C8L, 0x1E376C085141AB53L,
    0x2748774CDF8EEB99L, 0x34B0BCB5E19B48A8L,
    0x391C0CB3C5C95A63L, 0x4ED8AA4AE3418ACBL,
    0x5B9CCA4F7763E373L, 0x682E6FF3D6B2B8A3L,
    0x748F82EE5DEFB2FCL, 0x78A5636F43172F60L,
    0x84C87814A1F0AB72L, 0x8CC702081A6439ECL,
    0x90BEFFFA23631E28L, 0xA4506CEBDE82BDE9L,
    0xBEF9A3F7B2C67915L, 0xC67178F2E372532BL,
    0xCA273ECEEA26619CL, 0xD186B8C721C0C207L,
    0xEADA7DD6CDE0EB1EL, 0xF57D4F7FEE6ED178L,
    0x06F067AA72176FBAL, 0x0A637DC5A2C898A6L,
    0x113F9804BEF90DAEL, 0x1B710B35131C471BL,
    0x28DB77F523047D84L, 0x32CAAB7B40C72493L,
    0x3C9EBE0A15C9BEBCL, 0x431D67C49C100D4CL,
    0x4CC5D4BECB3E42B6L, 0x597F299CFC657E2AL,
    0x5FCB6FAB3AD6FAECL, 0x6C44198C4A475817L
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

  private static long S0(long x) {
    return rotateRight(x, 1) ^ rotateRight(x, 8) ^ (x >>> 7);
  }

  private static long S1(long x) {
    return rotateRight(x, 19) ^ rotateRight(x, 61) ^ (x >>> 6);
  }

  private static long S2(long x) {
    return rotateRight(x, 28) ^ rotateRight(x, 34) ^ rotateRight(x, 39);
  }

  private static long S3(long x) {
    return rotateRight(x, 14) ^ rotateRight(x, 18) ^ rotateRight(x, 41);
  }

  private static long F0(long x, long y, long z) {
    return ((x & y) | (z & (x | y)));
  }

  private static long F1(long x, long y, long z) {
    return (z ^ (x & (y ^ z)));
  }

  private void process(byte[] data, int start) {
    long[] W = new long[80];
    for (int i = 0; i < 16; i++)
      W[i] = getUInt64BE(data, start + (i << 3));
    for (int i = 16; i < 80; i++)
      W[i] = S1(W[i - 2]) + W[i - 7] + S0(W[i - 15]) + W[i - 16];

    long A = state[0];
    long B = state[1];
    long C = state[2];
    long D = state[3];
    long E = state[4];
    long F = state[5];
    long G = state[6];
    long H = state[7];

    int i = 0;
    do {
      long temp1, temp2;

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

    total[0] += (long) ilen;
    if (total[0] < (long) ilen)
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
    long high = (total[0] >>> 61) | (total[1] << 3);
    long low = total[0] << 3;

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
