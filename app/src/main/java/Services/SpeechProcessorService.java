package Services;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.CoffeeRecognition;
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
    private List<String> requestAvailableCoffeePlaces;
    private List<Map<List<String>,String>> dictionaryListForCoffeeTranslations;


    public List<String> getRequestHotBeverages() {
        return requestHotBeverages;
    }


    public SpeechProcessorService(){
        okoaRequest = new ArrayList<>();
        requestHelp = new ArrayList<>();
        requestColdBeverages = new ArrayList<>();
        requestHotBeverages = new ArrayList<>();
        requestAvailableCoffeePlaces = new ArrayList<>();
        Greetings = new ArrayList<>();
        doubleshotRequest = new ArrayList<>();
        dictionaryListForCoffeeTranslations = new ArrayList<>();
    }

    public List<Map<List<String>,String>> getDictionaryListForCoffeeTranslations() {
        return dictionaryListForCoffeeTranslations;
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
            }else if(command.getDescription().contains("available")){
                String[] availPlaces = command.getName().split(",");
                requestAvailableCoffeePlaces = Arrays.asList(availPlaces);
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

    /**
     * Determines if the user requested a hot drink from Okoa
     * @param input
     * @return true if the user requested both
     * @return false otherwise
     */
    public Boolean isOkoaHot(String input){
        if(isOkoaRequested(input) && isHotBeverageRequired(input)){
            return true;
        }
        return false;
    }
    /**
     * Determines if the user requested a cold drink from Okoa
     * @param input
     * @return true if the user requested both
     * @return false otherwise
     */
    public Boolean isOkoaCold(String input){
        if(isOkoaRequested(input) && isColdBeverageRequired(input)){
            return true;
        }
        return false;
    }
    /**
     * Determines if the user requested a hot drink from Doubleshot
     * @param input
     * @return true if the user requested both
     * @return false otherwise
     */
    public Boolean isDoubleshotHot(String input){
        if(isDoubleshotRequested(input) && isHotBeverageRequired(input)){
            return true;
        }
        return false;
    }
    /**
     * Determines if the user requested a cold drink from Doubleshot
     * @param input
     * @return true if the user requested both
     * @return false otherwise
     */
    public Boolean isDoubleshotCold(String input){
        if(isDoubleshotRequested(input) && isColdBeverageRequired(input)){
            return true;
        }
        return false;
    }

    /**
     * Determines if the user requested to view supported coffee places on the app
     * @param input is the user command/sentence/request
     * @return true if they asked for supported places and false otherwise
     */
    public Boolean isAvailablePlaces(String input){
        int size = requestAvailableCoffeePlaces.size();
        String[] splitted = input.split(" ");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < splitted.length; j++) {
                if(splitted[j].equals(
                        requestAvailableCoffeePlaces.get(i))){
                    return true;
                }
            }
        }
        return false;
    }


    /********************************************************************************
     *                    Functionality related to Processing User                  *
     *                                                                              *
     ********************************************************************************/
    public void initializeBruceCoffeePlaceRecognition(DataSnapshot dataSnapshot){
        for (DataSnapshot snap :
                dataSnapshot.getChildren()) {
            CoffeeRecognition coffeeRecognition = snap.getValue(CoffeeRecognition.class);
            processCoffeeDescription(coffeeRecognition.getBruceIterpretationCoffeeNames(),
                    coffeeRecognition.getCoffeeName());
        }
    }

    /**
     * This method processes the possible ways in which Bruce can translate the users'
     * coffee name request.
     * @param bruceIterpretationCoffeeNames is the input with the possible strings of words
     *                                      which refer to individual coffee names
     * @param coffeeName is the actual name of the coffee
     */
    private void processCoffeeDescription(String bruceIterpretationCoffeeNames,
                                          String coffeeName) {
        String[] coffeeNamesInterpretations = bruceIterpretationCoffeeNames.split(",");
        List<String> intepretations  = Arrays.asList(coffeeNamesInterpretations);
        Map<List<String>, String> tempDict = new HashMap<>();
        tempDict.put(intepretations,coffeeName);
        dictionaryListForCoffeeTranslations.add(tempDict);
    }
}
