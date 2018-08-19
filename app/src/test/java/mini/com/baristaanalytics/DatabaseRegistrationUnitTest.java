package mini.com.baristaanalytics;

import android.content.Context;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import Services.AdminService;

import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class DatabaseRegistrationUnitTest {

    @Mock
    Context mMockContext;
    @Mock
    DatabaseReference reference;

    @Test
    public void database_AdminRegistration_ReturnsTrue() {
        String uname = "soy",pword = "1234",confirm = "1234",email="validemail@mail.com",age = "mo",phone="081475",
                fname="mosaic",lname="mosaic";
        AdminService adminService = new AdminService();
        assertEquals(adminService.registerAdmin(mMockContext,reference,fname,lname,age,email,uname,pword,confirm,phone,"test"),"true");
    }
    // Test missing fields
    @Test
    public void database_AdminRegistration_ReturnsMissing() {
        String uname = "soy",pword = "1234",confirm = "1234",email = "",age = "mo",phone="081",
                fname="mosaic",lname="mosaic";
        AdminService adminService = new AdminService();
        assertEquals(adminService.registerAdmin(mMockContext,reference,fname,lname,age,email,uname,pword,confirm,phone,"test"),"missing");
    }
    // Test confirmation of passwords
    @Test
    public void database_AdminRegistration_ReturnsConfirm() {
        String uname = "soy",pword = "12345",confirm = "1234",email="mail",age = "mo",phone="081",
                fname="mosaic",lname="mosaic";
        AdminService adminService = new AdminService();
        assertEquals(adminService.registerAdmin(mMockContext,reference,fname,lname,age,email,uname,pword,confirm,phone,"test"),"unequal");
    }
}
