package co.dlg.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by preetam on 27/11/2017.
 */
public class SampleJunitTest {

    @Test
    public void firstTest(){
        System.out.println("Test passed");
    }

    @Before
    public void initializeTest(){
    }

    @After
    public void testCleanUp(){
    }

}
