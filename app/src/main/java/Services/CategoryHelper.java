package Services;

import java.util.List;

import Model.Beverage;

public class CategoryHelper {
    /**
     * This method searches for a beverage and updates that beverages' properties
     * @param beverage
     * @param beveragesList
     */
    public static void replaceBeverage(Beverage beverage, List<Beverage> beveragesList) {
        for (int i = 0; i < beveragesList.size(); i++) {
            String bev = beverage.getBeverage_name().toLowerCase();
            if(bev.equals(beveragesList.get(i).getBeverage_name())){
                beverage.setBeverage_name(bev);
                beveragesList.set(i,beverage);
            }
        }
    }

    /**
     * Check if the beverage already exists in our firebase array
     * @param beverage: the beverage retrieved
     * @return true : if the beverage already exists
     * @return false : if the beverage does not exist
     */
    public static Boolean beverageExists(List<String> coffeeNames,Beverage beverage){
        String tempCoffeeName = beverage.getBeverage_name().toLowerCase();
        if(coffeeNames.size()>0){
            for (int i = 0; i < coffeeNames.size(); i++) {
                if(coffeeNames.contains(tempCoffeeName)){
                    return true;
                }
            }
        }
        return false;
    }
}
