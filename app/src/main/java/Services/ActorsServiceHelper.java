package Services;

import android.media.MediaCodec;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is to contain methods which are common across all services to reduce code redundancies
public class ActorsServiceHelper {
    // static regex's since they does not change
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static final String PHONE_REGEX = "[0](\\d{9})|([0](\\d{2})( |-)((\\d{3}))( |-)(\\d{4}))|[0](\\d{2})( |-)(\\d{7})";
    // Static Pattern object, since phone_pattern is fixed
    private static Pattern email_pattern;
    private static Pattern phone_pattern;
    // non-static Matcher object because it's created from the input string
    private Matcher email_matcher;
    private Matcher phone_matcher;
    // Static
    public ActorsServiceHelper(){
        // initialize the phone_pattern object
        email_pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        phone_pattern = Pattern.compile(PHONE_REGEX, Pattern.CASE_INSENSITIVE);
    }

    /**
     * This method validates the input email address with the EMAIL_REGEX phone_pattern
     * @param email
     * @return boolean
     * */
    public Boolean validate_email(String email){
        email_matcher = email_pattern.matcher(email);
        return email_matcher.matches();
    }

    /**
     * This method validates the users' phone number by:
     * 1. Checking if the length is equal to 10
     * Uses the regular expression on this website:
     */
    public Boolean validate_phone(EditText phone){
        phone_matcher = phone_pattern.matcher(phone.getText().toString());
        return phone_matcher.matches();
    }

    /**
     * This method validates empty fields on each Edittext
     * @param registration_details
     * @return Boolean is_valid
     */
    public Boolean validate_edittext(ArrayList<EditText> registration_details){
        Boolean flag = true;
        int size = registration_details.size();
        for (int i = 0; i < size; i++) {
            if(registration_details.get(i).getText().toString().isEmpty()){
                registration_details.get(i).requestFocus();
                registration_details.get(i).setError("This field cannot be blank");
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * This method confirms the users' password
     * @param password,confirm_password
     * @return flag
     */
    public Boolean validate_password(EditText password, EditText confirm_password){
        Boolean flag = false;
        if(password.getText().toString().equals(confirm_password.getText().toString())){
            flag = true;
        }else {
            confirm_password.requestFocus();
            confirm_password.setError("Passwords do not match");
        }
        return flag;
    }
}