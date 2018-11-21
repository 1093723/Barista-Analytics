package mini.com.baristaanalytics;

import android.support.test.filters.SmallTest;

import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.runners.MockitoJUnitRunner;

import utilities.MessageItem;

import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;

// @RunWith(MockitoJUnitRunner.class)
public class RecyclerViewTests {
    @Test
    @SmallTest
    public void add_message_item_test(){
        String text = "Hello";
        MessageItem item = new MessageItem(text);
        assertEquals("Hello",item.getText());

    }
}
