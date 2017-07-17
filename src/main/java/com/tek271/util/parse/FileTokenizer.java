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

package com.tek271.util.parse;

import java.io.*;
import com.tek271.util.collections.list.*;
import com.tek271.util.io.*;
import com.tek271.util.string.StringUtility;

/**
<ol>
 <li>Extends the <code>Tokenizer</code> class.</li>
 <li>Tokenizes a text file, reading the syntax definition from another file.</li>
</ol>
 * <p>Copyright (c) 2004 Technology Exponent</p>
 * @author Abdul Habra
 * @version 1.0
 */
public class FileTokenizer extends Tokenizer {
  private String pConfigFile= StringUtility.EMPTY;
  private String pSourceFile= StringUtility.EMPTY;

  public FileTokenizer() {}

/**
 * create a tokenizer that reads the syntax from aConfigFile, and tokenizes aSourceFile
 * @param aConfigFile The path to the file that defines the syntax for the tokenizer.
 * @param aSourceFile String The file to be tokenized.
 * @throws IOException thrown if cannot access on of the files.
 */
  public FileTokenizer(final String aConfigFile, final String aSourceFile)
         throws IOException {
    setConfigFile(aConfigFile);
    setSourceFile(aSourceFile);
  }  // FileTokenizer

  public String getConfigFile() {
    return pConfigFile;
  }

  public void setConfigFile(final String aConfigFile) throws IOException {
    if (! FileIO.exists(aConfigFile))
      throw new IOException("Configuration File " + aConfigFile + " not found.");
    pConfigFile= aConfigFile;
    readConfig();
  }  // setConfigFile();

  public void setSourceFile(final String aSourceFile) throws IOException {
    if (! FileIO.exists(aSourceFile))
      throw new IOException("Source File " + aSourceFile + " not found.");

    pSourceFile=aSourceFile;
    String src= FileIO.read(pSourceFile);
    setText(src);
  } // setSourceFile

  public String getSourceFile() {
    return pSourceFile;
  }

/** read syntax from config file */
  private void readConfig() throws IOException {
    ConfigFile cf= new ConfigFile(pConfigFile);
    cf.setCommentChars("#");
    setCaseSensitive( cf.getValueAsBoolean("isCaseSensitive", true) );
    setKeywords( readListValues(cf, "keyword.") );
    setStringMarkers( readListValues(cf, "stringMarker.") );
    setSeparators( readListValues(cf, "separator.") );
    setWhiteSpaces( readListValues(cf, "whiteSpace.") );
    setSingleLineCommentMarkers( readListValues(cf, "singleLineCommentMarker.") );
    setMultiLineCommentStarters( readListValues(cf, "multiLineCommentStarter.") );
    setMultiLineCommentEnders( readListValues(cf, "multiLineCommentEnder.") );
    setNewLineMarkers( readListValues(cf, "newLineMarker.") );
  }  // readConfig();

/** get the values for a certain prefix */
  private static ListOfString readListValues(final ConfigFile aConfigFile, final String aPrefix) {
    ListOfString names= aConfigFile.getAllNames(aPrefix);
    ListOfString r= new ListOfString();
    String v;
    for (int i=0, n=names.size(); i<n; i++) {
      v= aConfigFile.getValue(names.getItem(i)).trim();
      v= StringUtility.unescapeUnicode(v);
      r.add(v);
    }
    return r;
  }  // readListValues

/** for testing */
  public static void main(String[] args) throws IOException {
    String syntax= "tokenizer.test.properties";
    String code="code1.txtp";
    FileTokenizer ft= new FileTokenizer(syntax, code);
    ft.isReturnNewLineMarkers= false;
    ft.isReturnWhiteSpace= false;
    ft.isReturnComments= false;
    System.out.println("---- GRAMMER:");
    System.out.println(ft.toString());
    System.out.println("---- TOKENS:");
    System.out.println(ft.getList().toString());
  }

}  // FileTokenizer
