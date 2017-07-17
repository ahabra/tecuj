package com.tek271.util.io;

/*
Technology Exponent Common Utilities For Java (TECUJ)
Copyright (C) 2003,2004  Abdul Habra
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.reflect.ReflectUtil;
import com.tek271.util.string.StringUtility;

/**
 * Generic file i/o methods for reading, writing, listing, and finding files.
 * <p>Copyright (c) 2003-2004</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class FileIO {
  public static final String ILLEGAL_FILE_NAME_CHARS= "<>[]?/\\=+:;\",*|^";
  
  /**
  * Write a string to a file.
  * @param aFileName The name of the file to write to.
  * @param aValue The data to write to the file.
  * @param aAppend If true append to the end of the file. If false,
  * overwrite the file.
  * @param aNewLine A flag to indicate if an End Of Line marker should
  * be added at the end of the file.
  * @throws IOException
  */
  public static void write(String aFileName, String aValue,
                           boolean aAppend, boolean aNewLine) throws IOException {
    FileOutputStream fos = new FileOutputStream(aFileName, aAppend);
    PrintStream outputFile = new PrintStream(fos);
    if (aNewLine) outputFile.println(aValue);
    else outputFile.print(aValue);
    outputFile.close();
    fos.close();
  } // write

  /**
  * @see #write(String aFileName, String aValue, boolean aAppend, boolean aNewLine)
  */
  public static void write(String aFileName, String aValue, boolean aAppend) throws IOException {
    write(aFileName, aValue, aAppend, false);
  }

  /**
  * @see #write(String aFileName, String aValue, boolean aAppend, boolean aNewLine)
  */
  public static void write(String aFileName, String aValue) throws IOException {
    write(aFileName, aValue, false, false);
  }

  /**
  * @see #write(String aFileName, String aValue, boolean aAppend, boolean aNewLine)
  */
  public static void writeln(String aFileName, String aValue, boolean aAppend) throws IOException {
    write(aFileName, aValue, aAppend, true);
  }

  /**
  * @see #write(String aFileName, String aValue, boolean aAppend, boolean aNewLine)
  */
  public static void writeln(String aFileName, String aValue) throws IOException {
    write(aFileName, aValue, false, true);
  }

  /**
  * Read a file.
  * @param aFileName The name of the file to read.
  * @return The contents of the file as a string.
  * @throws IOException
  */
  public static String read(String aFileName) throws IOException{
    File f = new File(aFileName);
    FileReader fr = new FileReader(f);
    char[] buf = new char[(int) f.length()];
    fr.read(buf, 0, (int) f.length());
    fr.close();
    return new String(buf);
  } // read

/**
 * Read a file from file system, but does not throw a checked exception
 * @param filePath the path to the file
 * @return the text of the file
 * @throws RuntimeException if it cannot read the file
 */  
  public static String readNoCheckException(String filePath) {
    try {
      return read(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Could not read " + filePath, e);
    }
  }
  
  /**
  * Read a binary file.
  * @param aFileName The name of the file to read.
  * @return The contents of the file as an array of bytes.
  * @throws IOException
  */
  public static byte[] readBinary(String aFileName) throws IOException {
      File f = new File(aFileName);
      FileInputStream fis = new FileInputStream(f);
      BufferedInputStream in = new BufferedInputStream(fis);
      int len = (int) f.length();
      byte[] buf = new byte[len];
      in.read(buf, 0, len);
      in.close();
      return buf;
  }

  /**
   * Returns the length of the file.
   * @param aFileName The name of the file.
   * @return The length.
   */
  public static long size(String aFileName) {
    File f = new File(aFileName);
    return f.length();
  }

  /**
  * Tests if the file exists.
  * @param aFileName Name of file
  * @return boolean true if exists, false if not
  */
  public static boolean exists(String aFileName) {
    File f = new File(aFileName);
    return f.exists();
  } // exists

  /**
   * @return The current user's directory
   */
  public static String currentDirectory() {
    return System.getProperty("user.dir") + System.getProperty("file.separator");
  } // currentDirectory

  /**
   * Create a new directory
   * @param aDirectory The directory's name
   * @return true if succesful
   */
  public static boolean makeDir(String aDirectory) {
    return new File(aDirectory).mkdirs();
  } // makeDir()

  /**
   * Rename a file to a new name
   * @param aOld old file name
   * @param aNew new file name
   * @return true if succesful
   */
  public static boolean rename(String aOld, String aNew) {
    return new File(aOld).renameTo(new File(aNew));
  } // rename()

  /**
   * Delete a file
   * @param aName name of file to delete
   * @return true if succesful
   */
  public static boolean delete(String aName) {
    return new File(aName).delete();
  } // delete

/**
* Delete files fom aDirecotry, where file name starts with sFileNamePrefix.
* @param aDirectory String Starting directory
* @param aFileNamePrefix String Prefix of File names to be deleted
* @return boolean true if successful
*/
 public static boolean delete(String aDirectory, String aFileNamePrefix) {
    File dir= new File(aDirectory);
    File[] files = dir.listFiles();
    if (files==null) return false;
    for (int i=0; i<files.length; i++) {
      if (files[i].getName().startsWith(aFileNamePrefix))
        files[i].delete();
    }
    return true;
  } // delete()

  /**
  * Extract the extension from the file name, e.g. from pic1.jpg return jpg.
  * @param aFileName Name of file to get its extension
  * @return empty string if none found.
  */
  public static String getExtension(String aFileName) {
    return StringUtility.substringAfterLast(aFileName, ".");
  } // getExtension()

  private static int indexOfLastSlash(final String aFileWithPath) {
    // get the index of the last \ or /
    int p1 = aFileWithPath.lastIndexOf('\\');
    int p2 = aFileWithPath.lastIndexOf('/');
    return Math.max(p1, p2);
  }

  /**
  * Extract the parent directory from a path.
  * @param aFileWithPath A full path.
  * @return The parent directory.<br>
  * Example: getDirectory("a/b/c.d") will return "a/b/"
  */
  public static String getDirectory(String aFileWithPath) {
    int p= indexOfLastSlash(aFileWithPath);
    if (p==-1) return StringUtility.EMPTY;
    return StringUtility.left(aFileWithPath, p+1);
  } //  getDirectory()

  /**
   * Extract the file name from a path
   * @param aFileWithPath String A full path.
   * @return String The parent directory.<br>
  * Example: getFileName("a/b/c.d") will return "c.d"
   */
  public static String getFileName(String aFileWithPath) {
    int p= indexOfLastSlash(aFileWithPath);
    if (p==-1) return aFileWithPath;
    return StringUtility.substring(aFileWithPath, p+1);
  }

  /**
  * Replace back slashs with forward slashs
  * @param aPath a Path to convert
  * @return The converted path.
  */
  public static String replaceBackslash(final String aPath) {
    return aPath.replace('\\', '/');
  }

  /**
   * Get a list of all files and directories at a given path (not recursive).
   * @param aPath Path to the directory
   * @return a list of file names.
   */
  public static ListOfString listAll(final String aPath) {
    File path= new File(aPath);
    String[] files= path.list();
    ListOfString r= new ListOfString();
    if (files != null)  r.setArray(files);
    return r;
  } // listAll

/**
 * Make sure aPath ends with a slash except if it is blank
 * @param aPath String
 * @return String
 */
  public static String cleanPathEnd(final String aPath) {
    if (StringUtility.isBlank(aPath)) return StringUtility.EMPTY;
    char c= StringUtility.lastChar(aPath);
    if (c=='/' || c=='\\') return aPath;
    return aPath + '/';
  } // cleanPathEnd

  private static void listToBuf(final ListOfString aBuf,
                                String aPath,
                                final boolean aIsIncludeFiles,
                                final boolean aIsIncludeDirs,
                                final boolean aIsListFullName,
                                final boolean aIsIncludeSubDirs) {
    aPath= cleanPathEnd(aPath);
    ListOfString all= listAll(aPath);
    for (int i=0, n=all.size(); i<n; i++) {
      String fn= all.getItem(i);
      String full= aPath + fn;
      File file= new File(full);
      boolean isDir= file.isDirectory();
      if (aIsIncludeDirs && isDir) {
        aBuf.add(aIsListFullName? full : fn);
      } else
      if (aIsIncludeFiles && file.isFile()) {
        aBuf.add(aIsListFullName? full : fn);
      }
      if (aIsIncludeSubDirs && isDir) {
        listToBuf(aBuf, full, aIsIncludeFiles, aIsIncludeDirs,
                  aIsListFullName, aIsIncludeSubDirs);
      }
    }
  }  // listToBuf

  /**
   * List the files and directories at a given path.
   * @param aPath The starting path
   * @param aIsIncludeFiles Include files in the listing.
   * @param aIsIncludeDirs Include directories in the listing.
   * @param aIsListFullName List the full path name of the file or directory.
   * @param aIsIncludeSubDirs List, recursivly, subdirectories found.
   * @return a list of all found files and directories that match the given condition.
   */
  public static ListOfString list(final String aPath,
                                  final boolean aIsIncludeFiles,
                                  final boolean aIsIncludeDirs,
                                  final boolean aIsListFullName,
                                  final boolean aIsIncludeSubDirs) {
    ListOfString r= new ListOfString();
    listToBuf(r, aPath, aIsIncludeFiles, aIsIncludeDirs,
              aIsListFullName, aIsIncludeSubDirs);
    return r;
  }  // list


/**
 * Requires jdk >= 1.4
 * List the files and directories at a given path.
 * @param aPath The starting path
 * @param aFilter A regular expression mask for file names.
 * @param aIsIncludeFiles Include files in the listing.
 * @param aIsIncludeDirs Include directories in the listing.
 * @param aIsListFullName List the full path name of the file or directory.
 * @param aIsIncludeSubDirs List, recursivly, subdirectories found.
 * @return a list of all found files and directories that match the given condition.
 */
  public static ListOfString list(final String aPath,
                                  final String aFilter,
                                  final boolean aIsIncludeFiles,
                                  final boolean aIsIncludeDirs,
                                  final boolean aIsListFullName,
                                  final boolean aIsIncludeSubDirs) {
    ListOfString all= list(aPath, aIsIncludeFiles, aIsIncludeDirs,
                           aIsListFullName, aIsIncludeSubDirs);
    ListOfString r= new ListOfString();
    Pattern pat = Pattern.compile(aFilter);
    for (int i=0, n=all.size(); i<n; i++) {
      String fullName= all.getItem(i);
      String fn= getFileName( fullName );
      if (pat.matcher(fn).matches()) {
        r.add( aIsListFullName? fullName : fn);
      }
    }

    return r;
  }

/**
* Creates the file <code>aFileName</code> and writes the byte array values to the file.
* <br>author: Doug Estep, Abdul Habra.
* @param aFileName a file name including the complete directory path to the file.
* @param aValue the byte array to write.
* @param aIsAppend if true, then bytes will be written to the end of the file rather
*        than the beginning.
* @throws IOException thrown if file access fails.
*/
  public static void writeBytes(final String aFileName,
                                final byte[] aValue,
                                final boolean aIsAppend) throws IOException {
    FileOutputStream stream = new FileOutputStream(aFileName, aIsAppend);
    stream.write(aValue);
    stream.close();
  }  // writeBytes

/**
* Creates the file <code>aFileName</code> and writes the byte array values to the file.
* <br>author: Doug Estep, Abdul Habra.
* @param aFileName a file name including the complete directory path to the file.
* @param aValue the byte array to write.
* @throws IOException thrown if file access fails.
*/
  public static void writeBytes(final String aFileName,
                                final byte[] aValue) throws IOException {
    writeBytes(aFileName, aValue, false);
  } // writeBytes

/** read a file as a stream from the program's default directory directory */
  public static InputStream readFileToStream(final String aFileName) {
    return ReflectUtil.classLoader().getResourceAsStream(aFileName);
  }

/** read the given file from the context (class path ) */  
  public static String readTextFileFromContext(String fileName) throws IOException {
    InputStream inputStream= readFileToStream(fileName);
    if (inputStream==null) {
      throw new IOException(fileName + " could not be found");
    }
    try {
      return IOUtils.toString(inputStream);
    } catch (IOException e) {
      throw e;
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public static String packageToPath(Class clazz) {
    String pack= clazz.getPackage().getName();
    pack= StringUtils.replaceChars(pack, '.', '/');
    return pack + '/';
  }
  
  /** test */
  public static void main(String[] args) {
    String d= list("c:/temp/db", true, true, true, true).toString();
    System.out.println(d);
  }

}
