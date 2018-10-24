package utilities;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

    public class smsService {
        // Find your Account Sid and Token at twilio.com/user/account
        public static final String ACCOUNT_SID = "AC5e9e0e35b5d0d531f924a4de2ee22050";
        public static final String AUTH_TOKEN = "2430556a4ac271070776fdb90b7c730b";

        public static void sendSMS() {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);


            Message message = Message.creator(new PhoneNumber("+27843968418"),//to
                    new PhoneNumber("+27875508137"),//from
                    "Order is made").create();

            System.out.println(message.getSid());
        }
    }

