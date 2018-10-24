package utilities;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;

import java.io.IOException;

public class SMS {
    public void sendSMS(){
        AuthMethod auth = new TokenAuthMethod("ff1ccc60", "guMrsm45B7BlGOQw");
        NexmoClient client = new NexmoClient(auth);
        TextMessage message = new TextMessage("12622000030", "0843968418", "Hello from Nexmo!");
        SmsSubmissionResult[] responses = new SmsSubmissionResult[0];
        try {
                responses = client.getSmsClient().submitMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NexmoClientException e) {
            e.printStackTrace();
        }
        for (SmsSubmissionResult response : responses) {
            System.out.println(response);
        }
    }
}
