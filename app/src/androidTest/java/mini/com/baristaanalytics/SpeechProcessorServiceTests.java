package mini.com.baristaanalytics;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Services.SpeechProcessorService;

public class SpeechProcessorServiceTests {
    SpeechProcessorService speechProcessorService = new SpeechProcessorService();

    @Test
    public void isSingOrder(){
        String order = "I would like 1 small macchiato and 1 large capuccino";
        String orderSingle = "I would like 1 small macchiato";
        Assert.assertTrue(speechProcessorService.isSingleOrder(orderSingle));
        Assert.assertFalse(speechProcessorService.isSingleOrder(order));
    }
    @Test
    public void getCoffeeSize(){
        String order = "I would like one tall macchiato";
        String orderSmall = "I would like one small macchiato";
        Assert.assertEquals("small",speechProcessorService.getCoffeeSize(orderSmall));
    }
    @Test
    public void getCoffeeName(){
        String order = "I would like one tall macchiato";
        List<String> coffeeNames = new ArrayList<>();
        coffeeNames.add("macchiato");coffeeNames.add("Frozen Chocolate");coffeeNames.add("Cappuccino");
        Assert.assertEquals("macchiato",speechProcessorService.getCoffeeName(order,coffeeNames));
    }
    @Test
    public void isAvailableCoffeePlaces(){
        String request = "Show me available coffee places";
        List<String> availables = new ArrayList<>();
        availables.add("available");availables.add("places");availables.add("show");
        speechProcessorService.setRequestAvailableCoffeePlaces(availables);
        Assert.assertTrue(speechProcessorService.isAvailablePlaces(request));
    }

    @Test
    public void isGreeting(){
        String line = "hello bruce";
        List<String> availables = new ArrayList<>();
        availables.add("hey");availables.add("hi");availables.add("hello");
        speechProcessorService.setGreetings(availables);
        Assert.assertTrue(speechProcessorService.isGreeting(line));
    }

}
