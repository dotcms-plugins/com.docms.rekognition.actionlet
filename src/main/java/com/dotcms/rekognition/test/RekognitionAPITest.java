package com.dotcms.rekognition.test;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dotcms.rekognition.api.RekognitionApi;

public class RekognitionAPITest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @Test
  public void test() {

    File f = new File("src/main/java/com/dotcms/rekognition/test/granddot.jpg");
    
    System.err.println(f.getAbsolutePath());
    if(f.exists()){
      
      System.err.println(new RekognitionApi().detectLabels(f));
      
    }
    
    
    
  }

}
