package Services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.Command;
import mini.com.baristaanalytics.R;

public class SpeechProcessorService {
    private String TAG = "SpeechProcessorService";
    // List of words which trigger Bruce to show supported coffee places
    private List<String> okoaRequest;
    private List<String>doubleshotRequest;
    private List<String>Greetings;
    private List<String>requestHelp;
    private List<String>requestHotBeverages;
    private List<String>requestColdBeverages;

    public List<String> getOkoaRequest() {
        return okoaRequest;
    }

    public List<String> getDoubleshotRequest() {
        return doubleshotRequest;
    }

    public List<String> getGreetings() {
        return Greetings;
    }

    public List<String> getRequestHelp() {
        return requestHelp;
    }

    public List<String> getRequestHotBeverages() {
        return requestHotBeverages;
    }

    public List<String> getRequestColdBeverages() {
        return requestColdBeverages;
    }

    private String userInput;
    public SpeechProcessorService(){
        okoaRequest = new ArrayList<>();
    }

    public void initializeAcceptedCommands(DataSnapshot dataSnapshot){
        for (DataSnapshot snap :
                dataSnapshot.getChildren()) {
            Command command = snap.getValue(Command.class);
            if(command.getDescription().contains("okoa")){
                String[] okoaRequests = command.getName().split(",");
                okoaRequest = Arrays.asList(okoaRequests);
            }else if(command.getDescription().contains("shot")){
                String[] dblShotRequests = command.getName().split(",");
                doubleshotRequest = Arrays.asList(dblShotRequests);
            }
            else if(command.getDescription().contains("hello")){
                String[] greeting = command.getName().split(",");
                Greetings = Arrays.asList(greeting);
            }
            else if(command.getDescription().contains("help")){
                String[] help = command.getName().split(",");
                requestHelp = Arrays.asList(help);
            }
            else if(command.getDescription().contains("hot")){
                String[] hotbevs = command.getName().split(",");
                requestHotBeverages = Arrays.asList(hotbevs);
            }
            else if(command.getDescription().contains("cold")){
                String[] coldbevs = command.getName().split(",");
                requestColdBeverages = Arrays.asList(coldbevs);
            }
        }
    }

    /**
     * This method checks if the supplied 'input' parameter is a greeting
     * @param input
     * @return
     */
    public Boolean isGreeting(String input){
        String[] splitted = input.split(" ");
        int size = Greetings.size();
        Log.d(TAG,input);
        for (int i = 0; i < size; i++) {
            Log.d(TAG,Greetings.get(i));
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(Greetings.get(i))){
                    if(splitted.length > 3){
                        return false;
                    }
                    return true;
                }
            }

        }
        return false;
    }
    /**
     * This method determines if the user response 'input' parameter
     * is the user asking for help
     * @param input
     * @return
     */
    public Boolean isHelpRequired(String input){
        int size = requestHelp.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(requestHelp.get(i))){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method determines if the user response 'input' parameter
     * is the user requesting for a hot beverage
     * @param input
     * @return
     */
    public Boolean isHotBeverageRequired(String input){
        int size = requestHotBeverages.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(requestHotBeverages.get(i))){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method determines if the user response 'input' parameter
     * is the user requesting for a hot beverage
     * @param input
     * @return
     */
    public Boolean isColdBeverageRequired(String input){
        int size = requestColdBeverages.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(requestColdBeverages.get(i))){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method determines if the user requested for
     * okoa
     * @param input
     * @return
     */
    public Boolean isOkoaRequested(String input){
        int size = okoaRequest.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(okoaRequest.get(i))){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method determines if the user requested for
     * Doubleshot
     * @param input
     * @return
     */
    public Boolean isDoubleshotRequested(String input){
        int size = doubleshotRequest.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(doubleshotRequest.get(i))){
                    return true;
                }
            }
        }
        return false;
    }




}
