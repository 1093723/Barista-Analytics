package Model;

public class Beverage {
    private String beverage_name;
    private String beverage_description;
    private String beverage_image;
    private String beverage_category;
    private Long price_small;
    private Long price_tall;

    public Beverage() {
    }

    public Beverage(String beverage_name, String beverage_description, String beverage_image,
                    String beverage_category, Long price_small, Long price_tall) {
        this.beverage_name = beverage_name;
        this.beverage_description = beverage_description;
        this.beverage_image = beverage_image;
        this.beverage_category = beverage_category;
        this.price_small = price_small;
        this.price_tall = price_tall;
    }

    public String getBeverage_name() {
        return beverage_name;
    }

    public void setBeverage_name(String beverage_name) {
        this.beverage_name = beverage_name;
    }

    public String getBeverage_description() {
        return beverage_description;
    }

    public void setBeverage_description(String beverage_description) {
        this.beverage_description = beverage_description;
    }

    public String getBeverage_image() {
        return beverage_image;
    }

    public void setBeverage_image(String beverage_image) {
        this.beverage_image = beverage_image;
    }

    public String getBeverage_category() {
        return beverage_category;
    }

    public void setBeverage_category(String beverage_category) {
        this.beverage_category = beverage_category;
    }

    public Long getPrice_small() {
        return price_small;
    }

    public void setPrice_small(Long price_small) {
        this.price_small = price_small;
    }

    public Long getPrice_tall() {
        return price_tall;
    }

    public void setPrice_tall(Long price_tall) {
        this.price_tall = price_tall;
    }
}
