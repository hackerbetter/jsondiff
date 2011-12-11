package foodev.jsondiff;

import java.util.Arrays;
import java.util.Collection;

import org.codehaus.jackson.node.NullNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.gson.JsonObject;

@RunWith(value = Parameterized.class)
public class JsonPatchTest {

    
    public JsonPatchTest(Object hint) {

        JsonPatch.setHint(hint);

    }

    
    @Parameters
    public static Collection<Object[]> hints() {

        Object[][] data = new Object[][] { { new JsonObject() }, { NullNode.getInstance() } };
        return Arrays.asList(data);

    }   


    @Before
    public void noSetup() {
        
      JsonPatch.setHint(null);
      
    }


    @After
    public void noTearDown() {
        
        JsonPatch.setHint(null);
      
    }


    @Test
    public void testPrimAdd() {

        String n = JsonPatch.apply("{}", "{a:1}");
        Assert.assertEquals("{\"a\":1}", n);

    }


    @Test
    public void testPrimMerge() {

        String n = JsonPatch.apply("{}", "{~a:1}");
        Assert.assertEquals("{\"a\":1}", n);

    }


    @Test
    public void testPrimRemove() {

        String n = JsonPatch.apply("{a:1}", "{-a:0}");
        Assert.assertEquals("{}", n);

    }


    @Test
    public void testPrimChange() {

        String n = JsonPatch.apply("{a:1}", "{a:2}");
        Assert.assertEquals("{\"a\":2}", n);

    }


    @Test
    public void testPrimChangeMerge() {

        String n = JsonPatch.apply("{a:1}", "{~a:2}");
        Assert.assertEquals("{\"a\":2}", n);

    }


    @Test
    public void testNullAdd() {

        String n = JsonPatch.apply("{}", "{a:null}");
        Assert.assertEquals("{\"a\":null}", n);

    }


    @Test
    public void testNullMerge() {

        String n = JsonPatch.apply("{}", "{~a:null}");
        Assert.assertEquals("{\"a\":null}", n);

    }


    @Test
    public void testNullRemove() {

        String n = JsonPatch.apply("{a:null}", "{-a:0}");
        Assert.assertEquals("{}", n);

    }


    @Test
    public void testObjAdd() {

        String n = JsonPatch.apply("{a:1}", "{a:{}}");
        Assert.assertEquals("{\"a\":{}}", n);

    }


    @Test
    public void testObjRemove() {

        String n = JsonPatch.apply("{a:{}}", "{-a:{}}");
        Assert.assertEquals("{}", n);

    }


    @Test
    public void testObjMerge() {

        String n = JsonPatch.apply("{a:{b:1}}", "{~a:{c:2}}");
        Assert.assertEquals("{\"a\":{\"b\":1,\"c\":2}}", n);

    }


    @Test
    public void testObjMergeToPrim() {

        String n = JsonPatch.apply("{a:1}", "{~a:{b:1}}");
        Assert.assertEquals("{\"a\":{\"b\":1}}", n);

    }


    @Test
    public void testObjMergetToNull() {

        String n = JsonPatch.apply("{a:null}", "{~a:{b:1}}");
        Assert.assertEquals("{\"a\":{\"b\":1}}", n);

    }


    @Test
    public void testObjMergetToArr() {

        String n = JsonPatch.apply("{a:[1]}", "{~a:{b:1}}");
        Assert.assertEquals("{\"a\":{\"b\":1}}", n);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testArrayAddBad() {

        JsonPatch.apply("{}", "{\"a[bad]\": 2}");

    }


    @Test
    public void testArrayAddFull() {

        String n = JsonPatch.apply("{}", "{a:[0,1]}");
        Assert.assertEquals("{\"a\":[0,1]}", n);

    }


    @Test
    public void testArrayMergeFull() {

        String n = JsonPatch.apply("{}", "{~a:[0,1]}");
        Assert.assertEquals("{\"a\":[0,1]}", n);

    }


    @Test
    public void testArrayAddToEmpty() {

        String n = JsonPatch.apply("{a:[]}", "{\"a[+0]\":1}");
        Assert.assertEquals("{\"a\":[1]}", n);

    }


    @Test
    public void testArrayAddLast() {

        String n = JsonPatch.apply("{a:[0]}", "{\"a[+1]\":1}");
        Assert.assertEquals("{\"a\":[0,1]}", n);

    }


    @Test
    public void testArrayAddFirst() {

        String n = JsonPatch.apply("{a:[0]}", "{\"a[+0]\":1}");
        Assert.assertEquals("{\"a\":[1,0]}", n);

    }


    @Test
    public void testArrayInsertMiddle() {

        String n = JsonPatch.apply("{a:[0,1]}", "{\"a[+1]\":2}");
        Assert.assertEquals("{\"a\":[0,2,1]}", n);

    }


    @Test
    public void testArrRemoveToEmpty() {
        String n = JsonPatch.apply("{a:[0]}", "{\"-a[0]\":null}");
        Assert.assertEquals("{\"a\":[]}", n);
    }


    @Test
    public void testArrRemoveFirst() {
        String n = JsonPatch.apply("{a:[0,1]}", "{\"-a[0]\":null}");
        Assert.assertEquals("{\"a\":[1]}", n);
    }


    @Test
    public void testArrRemoveLast() {
        String n = JsonPatch.apply("{a:[0,1]}", "{\"-a[1]\":null}");
        Assert.assertEquals("{\"a\":[0]}", n);
    }


    @Test
    public void testArrRemoveMiddle() {
        String n = JsonPatch.apply("{a:[0,1,2]}", "{\"-a[1]\":null}");
        Assert.assertEquals("{\"a\":[0,2]}", n);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testArrRemoveInsertMiddle() {

        JsonPatch.apply("{a:[0,1,2]}", "{\"-a[+1]\":null}");
    }


    @Test
    public void testAddRemoveOrderMatters() {
        String n = JsonPatch.apply("{a:[0,1,2]}", "{\"-a[0]\":null,\"-a[1]\":null,\"a[+3]\":3}");
        Assert.assertEquals("{\"a\":[2,3]}", n);
    }


    @Test
    public void testAddRemoveOrderMatters2() {
        String n = JsonPatch.apply("{a:[{b:0},{b:1},{b:2},{b:3}]}",
                "{\"a[+3]\":{d:2},\"-a[0]\":0,\"-a[1]\":0,\"~a[2]\":{c:2}}");
        Assert.assertEquals("{\"a\":[{\"b\":2,\"c\":2},{\"d\":2},{\"b\":3}]}", n);
    }


    @Test
    public void testAddRemoveOrderMatters3() {
        String n = JsonPatch.apply("{a:[{b:0},{b:1},{b:2},{b:3}]}",
                "{\"a[+2]\":{d:2},\"-a[1]\":0,\"~a[2]\":{c:2}}");
        Assert.assertEquals("{\"a\":[{\"b\":0},{\"d\":2},{\"b\":2,\"c\":2},{\"b\":3}]}", n);
    }


    @Test
    public void testArrObjMerge() {
        String n = JsonPatch.apply("{a:[0,{b:1},3]}", "{\"~a[1]\":{c:2}}");
        Assert.assertEquals("{\"a\":[0,{\"b\":1,\"c\":2},3]}", n);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testArrObjMergeInsert() {

        JsonPatch.apply("{a:[0,{b:1},3]}", "{\"~a[+1]\":{c:2}}");

    }

    // test for Issue #7 but how can it happen to start off with?
    // Thanks to DrLansing for finding the problem.
    @Test
    public void testCompareArrays() {

        Assert.assertEquals(0, JsonPatch.compareArrays(true, null, null));
        Assert.assertEquals(-1, JsonPatch.compareArrays(true, Arrays.asList(1), null));
        Assert.assertEquals(1, JsonPatch.compareArrays(true, null, Arrays.asList(1)));
        
        Assert.assertEquals(0, JsonPatch.compareArrays(true, Arrays.asList(1,2,3), Arrays.asList(1,2,3)));
        Assert.assertEquals(-1, JsonPatch.compareArrays(true, Arrays.asList(1,2), Arrays.asList(1,2,3)));
        Assert.assertEquals(1, JsonPatch.compareArrays(true, Arrays.asList(1,2,3), Arrays.asList(1,2)));

        Assert.assertEquals(1, JsonPatch.compareArrays(true, Arrays.asList(1,3), Arrays.asList(1,2)));
        Assert.assertEquals(-1, JsonPatch.compareArrays(true, Arrays.asList(1,2), Arrays.asList(1,3)));

        Assert.assertEquals(-1, JsonPatch.compareArrays(false, Arrays.asList(1,3), Arrays.asList(1,2)));
        Assert.assertEquals(1, JsonPatch.compareArrays(false, Arrays.asList(1,2), Arrays.asList(1,3)));

    }

}
