package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import Services.ActorsServiceHelper;

public class ActorServiceHelperTests {
    @Test
    public void validateEmail(){
        String valid_email = "sond@live.com";
        String invalidEmail = "mosaic";
        ActorsServiceHelper serviceHelper =  new ActorsServiceHelper();
        Assert.assertFalse(serviceHelper.validate_email(invalidEmail));
        Assert.assertTrue(serviceHelper.validate_email(valid_email));
    }
}
