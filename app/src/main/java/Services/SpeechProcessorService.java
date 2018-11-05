package Services;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import mini.com.baristaanalytics.R;

public class SpeechProcessorService {
    // List of words which trigger Bruce to show supported coffee places
    private ArrayList<String> supportedCoffeePlacesRequests;
    private ArrayList<String> okoaCoffeePlacesRequests;
    private ArrayList<String> coffeePlacesRecommendations;
    private ArrayList<String> coffeePlacesCupSizes;

    private String userInput;
    public SpeechProcessorService(){

    }
    public SpeechProcessorService(String s, Context ctx) {
        String[] isSupportedCoffeePlaces = ctx.getResources().getStringArray(R.array.isSupportedCoffeePlaces);
        String[] isOkoaRequestedCoffeePlaces = ctx.getResources().getStringArray(R.array.isOkoaRequested);
        String[] isRecommendationCoffeePlaces = ctx.getResources().getStringArray(R.array.isRecommended);
        String[] isCoffeePlacesCupSizes = ctx.getResources().getStringArray(R.array.isCoffeeSize);
        userInput = s; //new ArrayList is only needed if you absolutely need an ArrayList
        supportedCoffeePlacesRequests = new ArrayList<>(Arrays.asList(isSupportedCoffeePlaces));
        okoaCoffeePlacesRequests = new ArrayList<>(Arrays.asList(isOkoaRequestedCoffeePlaces));
        coffeePlacesRecommendations = new ArrayList<>(Arrays.asList(isRecommendationCoffeePlaces));
        coffeePlacesCupSizes = new ArrayList<>(Arrays.asList(isCoffeePlacesCupSizes));
    }

    /**
     * check if the user requested for supported/available coffee places
     * @return true if this is the request
     * @return false otherwise
     */
    public Boolean isSupportedCoffeePlace(){
        for (int i = 0; i < supportedCoffeePlacesRequests.size(); i++) {
            if(userInput.contains(supportedCoffeePlacesRequests.get(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * check if the user requested to make an order from Okoa
     * @return true if they did
     * @return false otherwise
     */
    public Boolean isOkoaRequested(){
        for (int i = 0; i < okoaCoffeePlacesRequests.size(); i++) {
            if(userInput.contains(okoaCoffeePlacesRequests.get(i))){
                return true;
            }
        }
        return false;
    }
    /**
     * check if the user requested Bruce to make a recommendation
     * @return true if they did
     * @return false otherwise
     */
    public Boolean isCoffeeRecommendations(String type,String orderLine){
        orderLine = orderLine.toLowerCase();
        if(type.equals("test")){
            String[] recArray = {"best","best available","recommend", "what coffee", "which coffee"};
            ArrayList<String> coffeePlacesPreference = new ArrayList<>(Arrays.asList(recArray));

            for (int i = 0; i < coffeePlacesPreference.size(); i++) {
                if(orderLine.contains(coffeePlacesPreference.get(i))){
                    return true;
                }
            }
        }else {
            for (int i = 0; i < coffeePlacesRecommendations.size(); i++) {
                if(userInput.contains(coffeePlacesRecommendations.get(i))){
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * @param orderLine is the line containing the order from a user
     * Search for the coffee size that the user inputted
     * @return the size from the string
     * @return -1 if the coffee size is not found
     */
    public String getCoffeeSize(String type,String orderLine){
        ArrayList<String> tempOrder = new ArrayList<>(Arrays.asList(orderLine.split(" ")));
        if(type.equals("test")){
            String[] sizes = {"small","medium","large"};
            ArrayList<String> coffeePlacesCupSizes = new ArrayList<>(Arrays.asList(sizes));

            for (int i = 0; i < coffeePlacesCupSizes.size(); i++) {
                if(tempOrder.contains(coffeePlacesCupSizes.get(i))){
                    return coffeePlacesCupSizes.get(i);
                }
            }
        }else {

            for (int i = 0; i < coffeePlacesCupSizes.size(); i++) {
                if(tempOrder.contains(coffeePlacesCupSizes.get(i))){
                    return coffeePlacesCupSizes.get(i);
                }
            }
        }
        return "-1";
    }
    /**
     * @param input is the line containing each order per line
     * Search for the coffee quantity that the user requested
     * @return the quantity if it exists
     * @return -1 if the coffee quantity is not found
     */
    public int getCoffeeQuantity(String input){
        ArrayList<String> tempOrder = new ArrayList<>(Arrays.asList(input.split(" ")));

        for (int i = 0; i < tempOrder.size(); i++) {
            if(tempOrder.get(i).matches(".*\\d+.*")){
                return Integer.parseInt(tempOrder.get(i));
            }
        }
        return -1;
    }

}
