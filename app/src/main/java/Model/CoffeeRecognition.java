package Model;

public class CoffeeRecognition {
    private String coffeeName;
    // How Bruce will understand the name of the coffee,
    // i.e if Frappe -> then it'll be
    private String bruceIterpretationCoffeeNames;

    public CoffeeRecognition(String coffeeName, String bruceIterpretationCoffeeNames) {
        this.coffeeName = coffeeName;
        this.bruceIterpretationCoffeeNames = bruceIterpretationCoffeeNames;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public String getBruceIterpretationCoffeeNames() {
        return bruceIterpretationCoffeeNames;
    }

    public void setBruceIterpretationCoffeeNames(String bruceIterpretationCoffeeNames) {
        this.bruceIterpretationCoffeeNames = bruceIterpretationCoffeeNames;
    }

}
