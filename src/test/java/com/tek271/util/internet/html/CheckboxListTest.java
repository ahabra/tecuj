package com.tek271.util.internet.html;

import com.tek271.util.internet.html.CheckboxList.CheckboxInfo;
import com.tek271.util.internet.html.CheckboxList.ICheckboxListCallback;
import com.tek271.util.io.FileIO;
import com.tek271.util.string.StringUtility;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CheckboxListTest extends TestCase {

  private static final String[] TITLES= {
    "title1", "title2", "title3", "title4", "title5",
  };
  
  private static final String[] VALUES= {
    "1", "2", "3", "4", "5",
  };
  
  
  private static class CheckboxCallback implements ICheckboxListCallback {
    public CheckboxInfo getCheckboxInfo(int row) {
      CheckboxInfo checkboxInfo= new CheckboxInfo();
      checkboxInfo.id= "test_" + row;
      checkboxInfo.name= "test";
      checkboxInfo.value= VALUES[row];
      // checkboxInfo.onClick= "checkBoxClicked()";
      return checkboxInfo;
    }

    /** @param column */
    public String getTitle(int row, int column) {
      return TITLES[row];
    }
  }

  private static CheckboxList createCheckboxList() {
    CheckboxList checkboxList= new CheckboxList(new CheckboxCallback(), TITLES.length, 1);
    return checkboxList;
  }
  
  private static String normalize(String str) {
    str= StringUtils.trimToEmpty(str);
    str= StringUtility.remove(str, '\r');
    return str;
  }
  
  public void testToStringForOneColumn() throws IOException{
    CheckboxList checkboxList= createCheckboxList();
//    checkboxList.cssClass= "smallFont";
    checkboxList.height= "70px";
    checkboxList.width= "200px";
    
    String actual= checkboxList.toString();
    actual= normalize(actual);
    String expected= normalize(FileIO.readTextFileFromContext("CheckboxListTest.1.html"));
    assertEquals(expected, actual);
  }

  public void testToStringForThreeColumns() throws IOException{
    CheckboxList checkboxList= new CheckboxList(
        new ICheckboxListCallback() {
          public CheckboxInfo getCheckboxInfo(int row) {
            CheckboxInfo checkboxInfo= new CheckboxInfo();
            checkboxInfo.id= "test_" + row;
            checkboxInfo.name= "test";
            checkboxInfo.value= VALUES[row];
            // checkboxInfo.onClick= "checkBoxClicked()";
            return checkboxInfo;
          }

          public String getTitle(int row, int column) {
            return TITLES[row] + "," + column;
          }
        },
        TITLES.length, 3);

    checkboxList.height= "70px";
    checkboxList.width= "400px";
    String actual= normalize(checkboxList.toString());
    String expected= normalize(FileIO.readTextFileFromContext("CheckboxListTest.2.html"));
    assertEquals(expected, actual);
  }

}
