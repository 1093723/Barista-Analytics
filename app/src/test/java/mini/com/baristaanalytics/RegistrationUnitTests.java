package mini.com.baristaanalytics;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import Services.AdminService;
import Services.ServiceHelper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@RunWith(MockitoJUnitRunner.class)
public class RegistrationUnitTests {

    @Before public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
    @Mock
    DatabaseReference reference;

    /**
     * This method tests for a valid database registration
     *
    * */
    @Test
    public void database_AdminRegistration_ReturnsTrue() {
        String uname = "soy",pword = "1234",confirm = "1234",mail="validemail@mail.com",old = "21",phone_no="0814754016",
                fname="mosaic",lname="mosaic";

        AdminService adminService = new AdminService();
        final EditText username = Mockito.mock(EditText.class);
        Mockito.when(username.getText()).thenReturn(new MockEditable(uname));
        final EditText password = Mockito.mock(EditText.class);
        Mockito.when(password.getText()).thenReturn(new MockEditable(pword));
        final EditText email = Mockito.mock(EditText.class);
        Mockito.when(email.getText()).thenReturn(new MockEditable(mail));
        final EditText age = Mockito.mock(EditText.class);
        Mockito.when(age.getText()).thenReturn(new MockEditable(old));
        final EditText phone = Mockito.mock(EditText.class);
        Mockito.when(phone.getText()).thenReturn(new MockEditable(phone_no));
        final EditText firstname = Mockito.mock(EditText.class);
        Mockito.when(firstname.getText()).thenReturn(new MockEditable(fname));
        final EditText lastname = Mockito.mock(EditText.class);
        Mockito.when(lastname.getText()).thenReturn(new MockEditable(lname));
        assertTrue(adminService.registerAdmin(reference,firstname,lastname,age,email,username,password,phone));
    }
    // Test missing fields
    @Test
    public void textfield_validation_ReturnsFalse() {
        String uname = "soy",pword = "",mail="validemail@mail.com",old = "21",phone_no="0814754016",
                fname="mosaic",lname="mosaic";
        ArrayList<EditText>details = new ArrayList<>();
        final EditText username = Mockito.mock(EditText.class);
        Mockito.when(username.getText()).thenReturn(new MockEditable(uname));
        details.add(username);
        final EditText password = Mockito.mock(EditText.class);
        Mockito.when(password.getText()).thenReturn(new MockEditable(pword));
        details.add(password);
        final EditText email = Mockito.mock(EditText.class);
        Mockito.when(email.getText()).thenReturn(new MockEditable(mail));
        details.add(email);
        final EditText age = Mockito.mock(EditText.class);
        Mockito.when(age.getText()).thenReturn(new MockEditable(old));
        details.add(age);
        final EditText phone = Mockito.mock(EditText.class);
        Mockito.when(phone.getText()).thenReturn(new MockEditable(phone_no));
        details.add(phone);
        final EditText firstname = Mockito.mock(EditText.class);
        Mockito.when(firstname.getText()).thenReturn(new MockEditable(fname));
        details.add(firstname);
        final EditText lastname = Mockito.mock(EditText.class);
        Mockito.when(lastname.getText()).thenReturn(new MockEditable(lname));
        details.add(lastname);
        ServiceHelper helper = new ServiceHelper();

        assertFalse(helper.validate_edittext(details));
    }
    /**
     * Test valid textfield entry
     */
    @Test
    public void textfield_validation_ReturnsTrue(){
        String uname = "soy",pword = "password",mail="validemail@mail.com",old = "21",phone_no="0814754016",
                fname="mosaic",lname="mosaic";
        ArrayList<EditText>details = new ArrayList<>();
        final EditText username = Mockito.mock(EditText.class);
        Mockito.when(username.getText()).thenReturn(new MockEditable(uname));
        details.add(username);
        final EditText password = Mockito.mock(EditText.class);
        Mockito.when(password.getText()).thenReturn(new MockEditable(pword));
        details.add(password);
        final EditText email = Mockito.mock(EditText.class);
        Mockito.when(email.getText()).thenReturn(new MockEditable(mail));
        details.add(email);
        final EditText age = Mockito.mock(EditText.class);
        Mockito.when(age.getText()).thenReturn(new MockEditable(old));
        details.add(age);
        final EditText phone = Mockito.mock(EditText.class);
        Mockito.when(phone.getText()).thenReturn(new MockEditable(phone_no));
        details.add(phone);
        final EditText firstname = Mockito.mock(EditText.class);
        Mockito.when(firstname.getText()).thenReturn(new MockEditable(fname));
        details.add(firstname);
        final EditText lastname = Mockito.mock(EditText.class);
        Mockito.when(lastname.getText()).thenReturn(new MockEditable(lname));
        details.add(lastname);
        ServiceHelper helper = new ServiceHelper();

        assertTrue(helper.validate_edittext(details));
    }
    /**
     * Test confirmation of passwords
     */
    @Test
    public void password_validation_ReturnsFalse() {
        String pass = "soymosaic";
        String confirmpass = "soymisaic";
        final EditText password = Mockito.mock(EditText.class);
        Mockito.when(password.getText()).thenReturn(new MockEditable(confirmpass));

        final EditText confirm = Mockito.mock(EditText.class);
        Mockito.when(confirm.getText()).thenReturn(new MockEditable(pass));
        ServiceHelper helper = new ServiceHelper();
        assertFalse(helper.validate_password(password,confirm));
    }
    @Test
    public void password_validation_ReturnsTrue() {
        String pass = "soymosaic";
        String confirmpass = "soymosaic";
        final EditText password = Mockito.mock(EditText.class);
        Mockito.when(password.getText()).thenReturn(new MockEditable(confirmpass));

        final EditText confirm = Mockito.mock(EditText.class);
        Mockito.when(confirm.getText()).thenReturn(new MockEditable(pass));
        ServiceHelper helper = new ServiceHelper();
        assertTrue(helper.validate_password(password,confirm));
    }
    // Test validity of email address
    @Test
    public void user_email_validation_returns_false(){
        // Extra input after normal address
        String email="mail@live.com@gmail";
        // Incomplete address
        String email_two="mail@gmail.";
        // Symbols before the @
        String email_third="dlasnd!@#@live.com.";

        ServiceHelper helper = new ServiceHelper();
        assertFalse(helper.validate_email(email));
        assertFalse(helper.validate_email(email_two));
        assertFalse(helper.validate_email(email_third));
    }
    @Test
    public void user_email_validation_returns_true(){
        // the usual syntax
        String email="mail@live.com";
        // outlook with two dots
        String email_two="mail@live.co.uk";

        ServiceHelper helper = new ServiceHelper();
        assertTrue(helper.validate_email(email));
        assertTrue(helper.validate_email(email_two));
    }

    @Test
    public void user_phone_validation_returns_false(){
        // Length > 10
        final EditText editText = Mockito.mock(EditText.class);
        Mockito.when(editText.getText()).thenReturn(new MockEditable("081428866968"));
        // Length < 10
        final EditText phone_two = Mockito.mock(EditText.class);
        Mockito.when(phone_two.getText()).thenReturn(new MockEditable("072665"));
        // Correct length and 00
        final EditText phone_three= Mockito.mock(EditText.class);
        Mockito.when(phone_three.getText()).thenReturn(new MockEditable("00148866968"));

        ServiceHelper helper = new ServiceHelper();
        assertFalse(helper.validate_phone(editText));
        assertFalse(helper.validate_phone(phone_two));
        assertFalse(helper.validate_phone(phone_three));
    }

    class MockEditable implements Editable{
        private String str;
        public MockEditable(String str){
            this.str = str;
        }
        @Override @NonNull
        public String toString(){
            return str;
        }
        @Override
        public Editable replace(int i, int i1, CharSequence charSequence, int i2, int i3) {
            return this;
        }

        @Override
        public Editable replace(int i, int i1, CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable insert(int i, CharSequence charSequence, int i1, int i2) {
            return this;
        }

        @Override
        public Editable insert(int i, CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable delete(int i, int i1) {
            return this;
        }

        @Override
        public Editable append(CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable append(CharSequence charSequence, int i, int i1) {
            return this;
        }

        @Override
        public Editable append(char c) {
            return this;
        }

        @Override
        public void clear() {

        }

        @Override
        public void clearSpans() {

        }

        @Override
        public void setFilters(InputFilter[] inputFilters) {

        }

        @Override
        public InputFilter[] getFilters() {
            return new InputFilter[0];
        }

        @Override
        public void getChars(int i, int i1, char[] chars, int i2) {

        }

        @Override
        public void setSpan(Object o, int i, int i1, int i2) {

        }

        @Override
        public void removeSpan(Object o) {

        }

        @Override
        public <T> T[] getSpans(int i, int i1, Class<T> aClass) {
            return null;
        }

        @Override
        public int getSpanStart(Object o) {
            return 0;
        }

        @Override
        public int getSpanEnd(Object o) {
            return 0;
        }

        @Override
        public int getSpanFlags(Object o) {
            return 0;
        }

        @Override
        public int nextSpanTransition(int i, int i1, Class aClass) {
            return 0;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int i) {
            return 0;
        }

        @Override
        public CharSequence subSequence(int i, int i1) {
            return str;
        }
    }
}
