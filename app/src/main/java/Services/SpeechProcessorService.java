package Services;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Beverage;
import Model.Command;

public class SpeechProcessorService {
    private String TAG = "SpeechProcessorService";

    public void setOkoaRequest(List<String> okoaRequest) {
        this.okoaRequest = okoaRequest;
    }

    // List of words which trigger Bruce to show supported coffee places
    private List<String> okoaRequest;

    public void setDoubleshotRequest(List<String> doubleshotRequest) {
        this.doubleshotRequest = doubleshotRequest;
    }

    private List<String>doubleshotRequest;

    public void setGreetings(List<String> greetings) {
        Greetings = greetings;
    }

    private List<String>Greetings;

    public void setRequestHelp(List<String> requestHelp) {
        this.requestHelp = requestHelp;
    }

    private List<String>requestHelp;

    public void setRequestHotBeverages(List<String> requestHotBeverages) {
        this.requestHotBeverages = requestHotBeverages;
    }

    private List<String>requestHotBeverages;

    public void setRequestColdBeverages(List<String> requestColdBeverages) {
        this.requestColdBeverages = requestColdBeverages;
    }

    private List<String>requestColdBeverages;

    public void setRequestAvailableCoffeePlaces(List<String> requestAvailableCoffeePlaces) {
        this.requestAvailableCoffeePlaces = requestAvailableCoffeePlaces;
    }

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
    /*public void initializeBruceCoffeePlaceRecognition(DataSnapshot dataSnapshot){
        for (DataSnapshot snap :
                dataSnapshot.getChildren()) {
            CoffeeRecognition coffeeRecognition = snap.getValue(CoffeeRecognition.class);
            processCoffeeDescription(coffeeRecognition.getBruceIterpretationCoffeeNames(),
                    coffeeRecognition.getCoffeeName());
        }
    }*/

    /**
     * This method processes the possible ways in which Bruce can translate the users'
     * coffee name request.
     * @param bruceIterpretationCoffeeNames is the input with the possible strings of words
     *                                      which refer to individual coffee names
     * @param coffeeName is the actual name of the coffee
     */
    /*private void processCoffeeDescription(String bruceIterpretationCoffeeNames,
                                          String coffeeName) {
        String[] coffeeNamesInterpretations = bruceIterpretationCoffeeNames.split(",");
        List<String> intepretations  = Arrays.asList(coffeeNamesInterpretations);
        Map<List<String>, String> tempDict = new HashMap<>();
        tempDict.put(intepretations,coffeeName);
        dictionaryListForCoffeeTranslations.add(tempDict);
    }*/

    /**
     * Given a single request by a user,
     * this method will return the name of the coffee that is ordered
     * e.g input :'I would like 1 small ice tea'
     * e.g output: 'ice tea
     * @param userInput
     * @param coffeeNames
     * @return
     */
    public String getCoffeeName(String userInput, List<String> coffeeNames){
            for (int i = 0; i < coffeeNames.size(); i++) {
                if(userInput.contains(coffeeNames.get(i))){
                    return coffeeNames.get(i);
                }
            }

        return null;
    }
    /**
     * Given the name of the coffee, list of beverages available and the size of the coffee
     * ordered,
     * this method will return the price of the coffee
     * e.g input :'ice tea', 'small', {name='ice tea',small='25',tall='30'}
     * e.g output: 25
     * @param coffeeName
     * @param coffeeSize
     * @param beverages
     * @return price of ordered coffee or null for an invalid name
     */
    public Long getCoffeePrice(String coffeeName, String coffeeSize, List<Beverage> beverages){
        for (int i = 0; i < beverages.size(); i++) {
            if(beverages.get(i).getBeverage_name().equals(coffeeName)){
                if(coffeeSize.equals("small")){
                    return beverages.get(i).getPrice_small();
                }else if(coffeeSize.equals("tall")){
                    return beverages.get(i).getPrice_tall();
                }
            }
        }
        return null;
    }
    /**
     * Given the user order,
     * this method will return the size of the coffee ordered
     * e.g input : 'I would like 1 small americano'
     * e.g output: small
     * @param userInput
     * @return size of ordered coffee or null for no size specified
     */
    public String getCoffeeSize(String userInput){
        List<String> userSplitted = Arrays.asList(userInput.split(" "));
        for (int i = 0; i < userSplitted.size(); i++) {
            if(userSplitted.get(i).equals("small")){
                return "small";
            }else if(userSplitted.equals("tall")){
                return "tall";
            }
        }
        return null;
    }

    /**
     * Given a line from a user request, this method determines whether the user requested for one
     * or more coffee items
     * @param userInput
     * @return true if one order is requested
     * @return false otherwise
     */
    public boolean isSingleOrder(String userInput){
        return userInput.split("and").length>1?false:true;
    }

    public Integer getCoffeeQuantity(HashMap<String, Integer> matchWordsToNumber, String input) {
        List<String> splitted = Arrays.asList(input.split(" "));
        for (int i = 0; i <splitted.size(); i++) {
            String possibleSize = splitted.get(i);
            if(matchWordsToNumber.containsKey(possibleSize)){
                return matchWordsToNumber.get(possibleSize);
            }else if(TextUtils.isDigitsOnly(possibleSize)){
                return Integer.parseInt(possibleSize);
            }
        }
        return null;
    }
}
