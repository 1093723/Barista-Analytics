package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import Model.Admin;

public class AdminUnitTests {
    Admin globalAdmin = new Admin("Admin","Super","30","superadmin@gmail.com","superadmin","12345678","0814574016");
    @Test
    public void testSetUserName(){
        Admin admin = new Admin();
        String tempUname = "Mos";
        admin.setAdminUserName(tempUname);
        Assert.assertEquals("Mos",admin.getadminUserName());
    }
    @Test
    public void testSetAge(){
        Admin admin = new Admin();
        String tempUname = "21";
        admin.setAdminAge(tempUname);
        Assert.assertEquals("21",admin.getadminAge());
    }@Test
    public void testFirstName(){
        Admin admin = new Admin();
        String tempFName = "Moses";
        admin.setAdminFirstName(tempFName);
        Assert.assertEquals(tempFName,admin.getadminFirstName());
    }@Test
    public void testSetEmail(){
        Admin admin = new Admin();
        String tempEmail = "mo@gmail.com";
        admin.setAdminEmail(tempEmail);
        Assert.assertEquals(tempEmail,admin.getadminEmail());
    }@Test
    public void testSetPhone(){
        Admin admin = new Admin();
        String tempPhone = "0814754016";
        admin.setAdminPhoneNumber(tempPhone);
        Assert.assertEquals(tempPhone,admin.getadminPhoneNumber());
    }@Test
    public void testSetPassword(){
        Admin admin = new Admin();
        String tempPWord = "Password";
        admin.setAdminPassword(tempPWord);
        Assert.assertEquals(tempPWord,admin.getadminPassword());
    }@Test
    public void testSetLastName(){
        Admin admin = new Admin();
        String lastName = "Maluleke";
        admin.setAdminLastName(lastName);
        Assert.assertEquals(lastName,admin.getadminLastName());
    }



    @Test
    public void testGetUserName(){
        Assert.assertEquals("superadmin",globalAdmin.getadminUserName());
    }
    @Test
    public void testGetAge(){
        Assert.assertEquals("30",globalAdmin.getadminAge());
    }@Test
    public void testGetFName(){
        Assert.assertEquals("Admin",globalAdmin.getadminFirstName());
    }@Test
    public void testGetEmail(){
        Assert.assertEquals("superadmin@gmail.com",globalAdmin.getadminEmail());
    }@Test
    public void testGetPhone(){
        Assert.assertEquals("0814574016",globalAdmin.getadminPhoneNumber());
    }@Test
    public void testGetPassword(){
        Assert.assertEquals("12345678",globalAdmin.getadminPassword());
    }@Test
    public void testGetLastName(){
        Assert.assertEquals("Super",globalAdmin.getadminLastName());
    }


}
