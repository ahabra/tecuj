package com.tek271.util.io;

import com.tek271.util.collections.list.ListOfString;
import com.tek271.util.log.SimpleConsoleLogger;

import junit.framework.TestCase;

public class ConfigFileTest extends TestCase {
	// FIXME DO not hard code custom path
  private static final String TEST_RESOURCES = "/Users/abdul/_src/java/tools/tecuj/v2/tecuj/src/test/resources/";

  private static final String pTEXT= 
    "a=1\n" +
    "b=2\n" +
    "c=3";
  
  private ConfigFile createConfigFile(String text) {
    ListOfString list= ListOfString.createFromString(text, "\n");
    ConfigFile r= new ConfigFile(list);
    return r;
  }

  private ConfigFile createConfigFile() {
    return createConfigFile(pTEXT);
  }
  
  public void testClone() {
    ConfigFile cf= createConfigFile();
    cf.setCommentChars("-/");
    cf.setEqualChars(":");
    ConfigFile clon= (ConfigFile) cf.clone();
    
    assertNotNull(clon);
    assertEquals(cf.toString(), clon.toString());
    assertEquals(cf.getCommentChars(), clon.getCommentChars());
    assertEquals(cf.getEqualChars(), clon.getEqualChars());
  }
  
  public void testProcessLineTagsNoTags() {
    String text= "a=hello";
    ConfigFile cf= createConfigFile(text);
    ConfigFile.processLineTags(0, cf);
    assertEquals("hello", cf.getValue(0));
  }

  public void testProcessLineTagsTagWithin() {
    String text= "a=hello\n" +
        "b=${a} world"
    ;
    ConfigFile cf= createConfigFile(text);
    ConfigFile.processLineTags(1, cf);
    assertEquals("hello world", cf.getValue("b") );
  }

  public void testProcessLineTagsSystemProp() {
    String text= "a=hello\n" +
        "b=${user.name}"
    ;
    ConfigFile cf= createConfigFile(text);
    ConfigFile.processLineTags(1, cf);
    assertEquals(System.getProperty("user.name"), cf.getValue("b") );
  }
  
  public void testProcessIncludeFileNotExist() {
    String text= "includeFile=doesNotExist.properties";
    ConfigFile cf= createConfigFile(text);
    boolean r= ConfigFile.processIncludeFile(0, SimpleConsoleLogger.LOGGER, cf); 
    assertFalse(r);
  }
  
  public void testProcessIncludeFileExist() throws Exception {
    String includedFile= TEST_RESOURCES + "test1.properties";
    String includedText= "p1=v1\np2=v2";
    FileIO.write(includedFile, includedText);
    
    String text=
      "a=hello\n" +
      "includeFile=" + includedFile + "\n" +
    	"b=world" 
      ;
    ConfigFile cf= createConfigFile(text);
    boolean r= ConfigFile.processIncludeFile(1, SimpleConsoleLogger.LOGGER, cf);
    FileIO.delete(includedFile);
    assertTrue(r);
    assertEquals(4, cf.size());
    assertEquals("v1", cf.getValue("p1"));
  }

  public void testProcessIncludeManyFile() throws Exception {
    String includedFile1= TEST_RESOURCES + "test1.properties";
    String includedText1= "p1=v1\np2=v2";
    FileIO.write(includedFile1, includedText1);
    String includedFile2= TEST_RESOURCES + "test2.properties";
    String includedText2= "pa=va\npb=vb";
    FileIO.write(includedFile2, includedText2);
    
    String text=
      "a=hello\n" +
      "includeFile.1=" + includedFile1 + "\n" +
      "includeFile.2=" + includedFile2 + "\n" +
      "b=world" 
      ;
    ConfigFile cf= createConfigFile(text);
    boolean r1= ConfigFile.processIncludeFile(1, SimpleConsoleLogger.LOGGER, cf); 
    boolean r2= ConfigFile.processIncludeFile(3, SimpleConsoleLogger.LOGGER, cf); 
    FileIO.delete(includedFile1);
    FileIO.delete(includedFile2);
//    System.out.println(cf.toString());
    assertTrue(r1);
    assertTrue(r2);
    assertEquals(6, cf.size());
    assertEquals("v1", cf.getValue("p1"));
    assertEquals("vb", cf.getValue("pb"));
  }

  public void testProcess() throws Exception {
    String includedFile= TEST_RESOURCES + "test1.properties";
    String includedText= "p1=v1\np2=v2";
    FileIO.write(includedFile, includedText);
    
    String text=
      "a=hello\n" +
      "file=" + includedFile + "\n"+
      "includeFile=${file}\n" +
      "b=world" 
      ;
    ConfigFile cf= createConfigFile(text);
    boolean r= cf.process(true, true, SimpleConsoleLogger.LOGGER);
    FileIO.delete(includedFile);
    assertTrue(r);
    assertEquals(5, cf.size());
    assertEquals("v1", cf.getValue("p1"));
  }

  public void testProcessRecursive() throws Exception {
    String includedFile1= TEST_RESOURCES + "test1.properties";
    String includedText1= "p1=v1\np2=v2";
    FileIO.write(includedFile1, includedText1);
    
    String includedFile2= TEST_RESOURCES + "test2.properties";
    String includedText2= "pi=3.14\n" +
    		                  "includeFile=" + includedFile1;
    FileIO.write(includedFile2, includedText2);
    
    String text=
      "a=hello\n" +
      "file=" + includedFile2 + "\n"+
      "includeFile=${file}\n" +
      "b=world" 
      ;
    ConfigFile cf= createConfigFile(text);
    boolean r= cf.process(true, true, SimpleConsoleLogger.LOGGER);
    FileIO.delete(includedFile1);
    FileIO.delete(includedFile1);
//    System.out.println(cf.toString());
    assertTrue(r);
    assertEquals(6, cf.size());
    assertEquals("v1", cf.getValue("p1"));
  }

}
