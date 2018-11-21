package mini.com.baristaanalytics;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import utilities.MessageItem;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecyclerViewTests {

    /* @Rule
    public ActivityTestRule<LoginActivity> main = new ActivityTestRule<>(LoginActivity.class); */

    @Test
    @SmallTest
    public void add_message_item_test(){
        String text = "Hello";
        MessageItem item = new MessageItem(text);
        assertEquals("Hello",item.getText());

    }
}
