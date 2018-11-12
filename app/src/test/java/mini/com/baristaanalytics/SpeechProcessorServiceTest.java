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
        //Assert.assertEquals(1, speechProcessorService.getCoffeeQuantity(orderLine[0]));
        //Assert.assertEquals(3, speechProcessorService.getCoffeeQuantity(orderLine[1]));
    }

    @Test
    public void getCoffeeSize(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String order = "I want to have 1 small frappe and 3 large americanos";
        String[] orderLine = order.split("and");
        //Assert.assertEquals("small", speechProcessorService.getCoffeeSize("test",orderLine[0]));
    }

    @Test
    public void testRecommendationRequestPositive(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String preference_firs = "What's the best coffee place to consider on this app?";
        String preference_sec = "Recommend a coffee place";
        //Assert.assertTrue(speechProcessorService.isCoffeeRecommendations("test",preference_firs));
        //Assert.assertTrue(speechProcessorService.isCoffeeRecommendations("test",preference_sec));
    }
    @Test
    public void testRecommendationRequestNegative(){
        SpeechProcessorService speechProcessorService = new SpeechProcessorService();
        String preference_firs = "What's the best coffee place to consider on this app?";
        String preference_sec = "Recommend a coffee place";
        String preference_third = "Tell me a riddle";
        //Assert.assertTrue(speechProcessorService.isCoffeeRecommendations("test",preference_firs));
    }
}
