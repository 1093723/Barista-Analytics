package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import Model.Beverage;

public class BeverageUnitTests {
    Beverage beverage = new Beverage("Macchiato","A sweet delicacy of nespresso with honey",
                            "www.macchiato.com/mach.jpg","hot",Float.valueOf(0),Long.valueOf(20),
                            "22",Long.valueOf(25));

    @Test
    public void testGetBeverageName(){
        Assert.assertEquals("Macchiato",beverage.getBeverage_name());
    }
    @Test
    public void testGetBeverageDesc(){
        Assert.assertEquals("A sweet delicacy of nespresso with honey",
                beverage.getBeverage_description());
    }@Test
    public void testGetBeverageImage(){
        Assert.assertEquals("www.macchiato.com/mach.jpg",beverage.getBeverage_image());
    }@Test
    public void testGetBeverageRating(){
        Assert.assertEquals(Float.valueOf(0),beverage.getBeverage_rating());
    }@Test
    public void testGetBeverageCategory(){
        Assert.assertEquals("hot",beverage.getBeverage_category());
    }@Test
    public void testGetBeveragePriceSmall(){
        Assert.assertEquals(Long.valueOf(20),beverage.getPrice_small());
    }@Test
    public void testGetBeveragePriceTall(){
        Assert.assertEquals(Long.valueOf(25),beverage.getPrice_tall());
    }



    @Test
    public void testSetBeverageName(){
        Beverage temp = new Beverage();

        Assert.assertEquals("Macchiato",beverage.getBeverage_name());
    }
    @Test
    public void testSetBeverageDesc(){
        Beverage temp = new Beverage();
        temp.setBeverage_description("A sweet delicacy of nespresso with honey");
        Assert.assertEquals("A sweet delicacy of nespresso with honey",
                temp.getBeverage_description());
    }@Test
    public void testSetBeverageImage(){
        Beverage temp = new Beverage();
        temp.setBeverage_image("www.macchiato.com/mach.jpg");
        Assert.assertEquals("www.macchiato.com/mach.jpg",temp.getBeverage_image());
    }@Test
    public void testSetBeverageRating(){
        Beverage temp = new Beverage();
        temp.setBeverage_rating(Float.valueOf(0));
        Assert.assertEquals(Float.valueOf(0),temp.getBeverage_rating());
    }@Test
    public void testSetBeverageCategory(){
        Beverage temp = new Beverage();
        temp.setBeverage_category("hot");
        Assert.assertEquals("hot",temp.getBeverage_category());
    }@Test
    public void testSetBeveragePriceSmall(){
        Beverage temp = new Beverage();
        temp.setPrice_small(Long.valueOf(0));
        Assert.assertEquals(Long.valueOf(0),temp.getPrice_small());
    }@Test
    public void testSetBeveragePriceTall(){
        Beverage temp = new Beverage();
        temp.setPrice_tall(Long.valueOf(25));
        Assert.assertEquals(Long.valueOf(25),temp.getPrice_tall());
    }
}
