package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import Model.Admin;
import Model.Customer;

public class CustomerUnitTests {
    Customer globalCustomer = new Customer("Customer","Super",
            "30","superCustomer@gmail.com","superCustomer"
            ,"12345678","0814574016");
    @Test
    public void testSetUserName(){
        Customer customer = new Customer();
        String tempUname = "Mos";
        customer.setCustomerUserName(tempUname);
        Assert.assertEquals("Mos",customer.getCustomerUserName());
    }
    @Test
    public void testSetAge(){
        Customer admin = new Customer();
        String tempUname = "21";
        admin.setCustomerAge(tempUname);
        Assert.assertEquals("21",admin.getCustomerAge());
    }@Test
    public void testFirstName(){
        Customer admin = new Customer();
        String tempFName = "Moses";
        admin.setCustomerFirstName(tempFName);
        Assert.assertEquals(tempFName,admin.getCustomerFirstName());
    }@Test
    public void testSetEmail(){
        Customer admin = new Customer();
        String tempEmail = "mo@gmail.com";
        admin.setCustomerEmail(tempEmail);
        Assert.assertEquals(tempEmail,admin.getCustomerEmail());
    }@Test
    public void testSetPhone(){
        Customer admin = new Customer();
        String tempPhone = "0814754016";
        admin.setCustomerPhoneNumber(tempPhone);
        Assert.assertEquals(tempPhone,admin.getCustomerPhoneNumber());
    }@Test
    public void testSetPassword(){
        Customer admin = new Customer();
        String tempPWord = "Password";
        admin.setCustomerPassword(tempPWord);
        Assert.assertEquals(tempPWord,admin.getCustomerPassword());
    }@Test
    public void testSetLastName(){
        Customer admin = new Customer();
        String lastName = "Maluleke";
        admin.setCustomerLastName(lastName);
        Assert.assertEquals(lastName,admin.getCustomerLastName());
    }



    @Test
    public void testGetUserName(){
        Assert.assertEquals("superCustomer", globalCustomer.getCustomerUserName());
    }
    @Test
    public void testGetAge(){
        Assert.assertEquals("30", globalCustomer.getCustomerAge());
    }@Test
    public void testGetFName(){
        Assert.assertEquals("Customer", globalCustomer.getCustomerFirstName());
    }@Test
    public void testGetEmail(){
        Assert.assertEquals("superCustomer@gmail.com", globalCustomer.getCustomerEmail());
    }@Test
    public void testGetPhone(){
        Assert.assertEquals("0814574016", globalCustomer.getCustomerPhoneNumber());
    }@Test
    public void testGetPassword(){
        Assert.assertEquals("12345678", globalCustomer.getCustomerPassword());
    }@Test
    public void testGetLastName(){
        Assert.assertEquals("Super", globalCustomer.getCustomerLastName());
    }

}
