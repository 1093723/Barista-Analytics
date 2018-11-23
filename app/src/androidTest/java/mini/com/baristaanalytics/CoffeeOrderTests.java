package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import Model.CoffeeOrder;

public class CoffeeOrderTests {
    CoffeeOrder coffeeOrder = new CoffeeOrder(1,"adndabhlsvbsfv2412","1x Small Capuccino"
            ,"1093723",Long.valueOf(25),"23/Nov/2018",
            "Ordered",Float.valueOf(2),"Okoa Coffee Co.");

    @Test
    public void testGetOrder_Store(){
        Assert.assertEquals("Okoa Coffee Co.",coffeeOrder.getOrder_Store());
    }

    @Test
    public void testGetOrder_Username(){
        Assert.assertEquals("1093723",coffeeOrder.getOrder_CustomerUsername());
    }

    @Test
    public void testGetOrder_Date(){
        Assert.assertEquals("23/Nov/2018",coffeeOrder.getOrder_Date());
    }

    @Test
    public void testGetOrder_State(){
        Assert.assertEquals("Ordered",coffeeOrder.getOrder_State());
    }

    @Test
    public void testGetOrder_Description(){
        Assert.assertEquals("1x Small Capuccino",coffeeOrder.getOrder_Description());
    }

    @Test
    public void testGetOrder_Rating(){
        Assert.assertEquals(Float.valueOf(2),coffeeOrder.getOrder_Rating());
    }@Test
    public void testGetOrder_UUID(){
        Assert.assertEquals("adndabhlsvbsfv2412",coffeeOrder.getUUID());
    }




    @Test
    public void testSetOrder_Store(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_Store("Doubleshot Coffee & Tea");
        Assert.assertEquals("Doubleshot Coffee & Tea",order.getOrder_Store());
    }

    @Test
    public void testSetOrder_Username(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_CustomerUsername("1075502");
        Assert.assertEquals("1075502",order.getOrder_CustomerUsername());
    }

    @Test
    public void testSetOrder_Date(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_Date("22/11/2014");
        Assert.assertEquals("22/11/2014",order.getOrder_Date());
    }

    @Test
    public void testSetOrder_State(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_State("Confirmed");
        Assert.assertEquals("Confirmed",order.getOrder_State());
    }

    @Test
    public void testSetOrder_Description(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_Description("2x Large Macchiate");
        Assert.assertEquals("2x Large Macchiate",order.getOrder_Description());
    }

    @Test
    public void testSetOrder_Rating(){
        CoffeeOrder order = new CoffeeOrder();
        order.setOrder_Rating(Float.valueOf(1));
        Assert.assertEquals(Float.valueOf(1),order.getOrder_Rating());
    }@Test
    public void testSetOrder_UUID(){
        CoffeeOrder order = new CoffeeOrder();
        order.setUUID("24325145");
        Assert.assertEquals("24325145",order.getUUID());
    }
}
