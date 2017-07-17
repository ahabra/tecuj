package com.tek271.util.io;

/*
 Technology Exponent Common Utilities For Java (TECUJ)
 Copyright (C) 2003,2007  Abdul Habra, Doug Estep
 www.tek271.com

 This file is part of TECUJ.

 TECUJ is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; version 2.

 TECUJ is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with TECUJ; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 You can contact the author at ahabra@yahoo.com
 */

import java.io.*;
import java.util.zip.*;
import com.tek271.util.collections.array.ArrayUtilities;

/**
 * <p>Copyright (c) 2007 Technology Exponent</p>
 * @author Doug Estep
 * @version 1.0
 * @since 0.2
 */
public class Compress {
  private static final int BUFFER_SIZE= 1024;

  /**
   * Compresses the given array using the compression level specified.
   * @param aBytes the data to compress. If null, null is returned.
   * @param aLevel the compression level.  See the java.util.zip.Deflater class for possible levels.
   * @return the compressed data or null if the input was null or had no length.
   * @throws IOException thrown if the compression fails.
   */
  public static byte[] compress(final byte[] aBytes, final int aLevel) throws IOException {
    if (ArrayUtilities.isEmpty(aBytes))  return null;

    Deflater compressor= new Deflater();
    compressor.setLevel(aLevel);
    compressor.setInput(aBytes);
    compressor.finish();

    ByteArrayOutputStream bos= new ByteArrayOutputStream(aBytes.length);
    byte[] buf= new byte[BUFFER_SIZE];
    while (!compressor.finished()) {
      int count=compressor.deflate(buf);
      bos.write(buf, 0, count);
    }
    bos.close();
    return bos.toByteArray();
  }

  /**
   * Compresses the given array using the Deflater.BEST_COMPRESSION compression level.
   * @param aBytes the data to compress. If null, null is returned.
   * @return the compressed data or null if the input was null or had no length.
   * @throws IOException thrown if the compression fails.
   */
  public static byte[] compress(final byte[] aBytes) throws IOException {
    return compress(aBytes, Deflater.BEST_COMPRESSION);
  }

  /**
   * Decompresses data that was compressed via the compress method of this class.
   * @param aBytes the compressed data to decompress.
   * @return the decompressed data or null if the input was null or had no length.
   * @throws IOException thrown if the decompression fails.
   * @throws DataFormatException if the compressed data format is invalid
   */
  public static byte[] decompress(final byte[] aBytes) throws IOException, DataFormatException {
    if (ArrayUtilities.isEmpty(aBytes))  return null;

    Inflater decompressor=new Inflater();
    decompressor.setInput(aBytes);

    ByteArrayOutputStream bos=new ByteArrayOutputStream(aBytes.length);
    byte[] buf=new byte[BUFFER_SIZE];
    while (!decompressor.finished()) {
      int count=decompressor.inflate(buf);
      bos.write(buf, 0, count);
    }
    bos.close();
    return bos.toByteArray();
  }

}
