package mini.com.baristaanalytics;


import junit.framework.Assert;

import org.junit.Test;

import Services.SpeechProcessorService;

public class SpeechProcessorServiceTest {

    @Test
    public void checkNumber(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String order = "I want to have 1 small frappe and 3 large americanos";
        String[] orderLine = order.split("and");
    }

    @Test
    public void getCoffeeSize(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String order = "I want to have 1 small frappe and 3 large americanos";
        String[] orderLine = order.split("and");
    }

    @Test
    public void testRecommendationRequestPositive(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String preference_firs = "What's the best coffee place to consider on this app?";
        String preference_sec = "Recommend a coffee place";
    }
    @Test
    public void testRecommendationRequestNegative(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String preference_firs = "What's the best coffee place to consider on this app?";
        String preference_sec = "Recommend a coffee place";
        String preference_third = "Tell me a riddle";
    }
}
