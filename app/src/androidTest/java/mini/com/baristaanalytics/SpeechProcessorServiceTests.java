package mini.com.baristaanalytics;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        String lineFalse = "awe";
        List<String> availables = new ArrayList<>();
        availables.add("hey");availables.add("hi");availables.add("hello");
        speechProcessorService.setGreetings(availables);
        Assert.assertTrue(speechProcessorService.isGreeting(line));
        Assert.assertFalse(speechProcessorService.isGreeting(lineFalse));
    }

    @Test
    public void isHelpRequired(){
        String line = "This app is very frustrating";
        String lineFalse = "I love this app";
        List<String> help = new ArrayList<>();
        help.add("frustrating");help.add("frustrated");help.add("frustrated");help.add("useless");
        speechProcessorService.setRequestHelp(help);
        Assert.assertTrue(speechProcessorService.isHelpRequired(line));
        Assert.assertFalse(speechProcessorService.isHelpRequired(lineFalse));
    }

    @Test
    public void isOkoaRequest(){
        String line = "I'd like something from okoa";
        String lineFalse = "I feel like coffee";
        List<String> okoa = new ArrayList<>();
        okoa.add("aqua");okoa.add("okoa");okoa.add("koa");
        speechProcessorService.setOkoaRequest(okoa);
        Assert.assertTrue(speechProcessorService.isOkoaRequested(line));
        Assert.assertFalse(speechProcessorService.isOkoaRequested(lineFalse));
    }

    @Test
    public void isDoubleshotRequest(){
        String line = "I'd like something from doubleshot";
        String lineFalse = "I feel like coffee";
        List<String> okoa = new ArrayList<>();
        okoa.add("double shot");okoa.add("shot");okoa.add("doubleshot");
        speechProcessorService.setDoubleshotRequest(okoa);
        Assert.assertTrue(speechProcessorService.isDoubleshotRequested(line));
        Assert.assertFalse(speechProcessorService.isDoubleshotRequested(lineFalse));
    }

    @Test
    public void isHotBeverage(){
        String line = "I'd like something warm";
        String lineFalse = "I feel like cold coffee";
        List<String> hot = new ArrayList<>();
        hot.add("hot");hot.add("warm");
        speechProcessorService.setRequestHotBeverages(hot);
        Assert.assertTrue(speechProcessorService.isHotBeverageRequired(line));
        Assert.assertFalse(speechProcessorService.isHotBeverageRequired(lineFalse));
    }

    @Test
    public void isOkoaHot(){
        String line = "I'd like something hot from okoa";
        String lineFalse = "I feel like okoa coffee";
        List<String> okoa = new ArrayList<>();
        okoa.add("coa");okoa.add("aqua");okoa.add("okoa");
        speechProcessorService.setOkoaRequest(okoa);

        List<String> hot = new ArrayList<>();
        hot.add("hot");hot.add("warm");
        speechProcessorService.setRequestHotBeverages(hot);

        Assert.assertTrue(speechProcessorService.isOkoaHot(line));
        Assert.assertFalse(speechProcessorService.isOkoaHot(lineFalse));
    }

    @Test
    public void isDoubleshotHot(){
        String line = "I'd like something hot from doubleshot";
        String lineFalse = "I feel like okoa coffee from doubleshot";
        List<String> okoa = new ArrayList<>();
        okoa.add("double shot");okoa.add("shot");okoa.add("doubleshot");
        speechProcessorService.setDoubleshotRequest(okoa);

        List<String> hot = new ArrayList<>();
        hot.add("hot");hot.add("warm");
        speechProcessorService.setRequestHotBeverages(hot);

        Assert.assertTrue(speechProcessorService.isDoubleshotHot(line));
        Assert.assertFalse(speechProcessorService.isDoubleshotHot(lineFalse));
    }

    @Test
    public void isOkoaCold(){
        String line = "I'd like something cool from okoa";
        String lineFalse = "I feel like okoa coffee";
        List<String> okoa = new ArrayList<>();
        okoa.add("koa");okoa.add("okoa");okoa.add("aqua");
        speechProcessorService.setOkoaRequest(okoa);

        List<String> cold = new ArrayList<>();
        cold.add("cold");cold.add("cool");
        speechProcessorService.setRequestColdBeverages(cold);

        Assert.assertTrue(speechProcessorService.isOkoaCold(line));
        Assert.assertFalse(speechProcessorService.isOkoaCold(lineFalse));
    }

    @Test
    public void isDoubleshotCold(){
        String line = "I'd like something cool from doubleshot";
        String lineFalse = "I feel like doubleshot coffee from doubleshot";

        List<String> doubleshot = new ArrayList<>();
        doubleshot.add("double shot");doubleshot.add("shot");doubleshot.add("doubleshot");
        speechProcessorService.setDoubleshotRequest(doubleshot);

        List<String> cold = new ArrayList<>();
        cold.add("cold");cold.add("cool");
        speechProcessorService.setRequestColdBeverages(cold);

        Assert.assertTrue(speechProcessorService.isDoubleshotCold(line));
        Assert.assertFalse(speechProcessorService.isDoubleshotCold(lineFalse));
    }

    @Test
    public void coffeeQuantityTest() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();

        List<String> wordCount = Arrays.asList(appContext.getResources().getStringArray(R.array.wordsUpToTen));
        HashMap<String,Integer> matchWordsToNumber = new HashMap<>();
        for (int i = 0; i < wordCount.size(); i++) {
            matchWordsToNumber.put(wordCount.get(i),i+1);
        }
        String order = "I would like one macchiato";
        String orderTrueA = "I would like three macchiato";
        String orderTrue ="I would like 1 macchiato";
        Integer one = 1;
        Integer three = 3;
        Assert.assertEquals(one,speechProcessorService.getCoffeeQuantity(matchWordsToNumber,order));
        Assert.assertEquals(one,speechProcessorService.getCoffeeQuantity(matchWordsToNumber,orderTrue));
        Assert.assertEquals(three,speechProcessorService.getCoffeeQuantity(matchWordsToNumber,orderTrueA));
    }
}
