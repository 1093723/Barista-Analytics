package Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.CoffeeOrder;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "Barista_Analytics";
    private Context ctx;
    private static final int DB_VER=1;
    public Database(Context context) {
        super(context, DB_NAME, null,DB_VER);
        this.ctx = context;

    }

    public static final String TAG = "Database";


    public Boolean databaseExists(Context ctx){
        Log.i(TAG," databaseExists() ");
        File file = ctx.getDatabasePath(DB_NAME);
        return file.exists();
    }
    public List<CoffeeOrder> getCarts(){
        Log.i(TAG," getCart() ");
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"order_ID","uuid","order_Description","order_CustomerUsername",
                                "order_Total","order_Date","order_State","order_Rating","order_Store"};
        String sqlTable = "CoffeeOrders";

        queryBuilder.setTables(sqlTable);
        Cursor c = queryBuilder.query(database,sqlSelect,
                null,null,null,null,null);
        final List<CoffeeOrder> result = new ArrayList<>();
        if( c != null){
            if(c.moveToFirst()){
                do{
                    result.add(new CoffeeOrder(Integer.parseInt(c.getString(c.getColumnIndex("order_ID"))),
                            c.getString(c.getColumnIndex("uuid")),
                            c.getString(c.getColumnIndex("order_Description")),
                            c.getString(c.getColumnIndex("order_CustomerUsername")),
                            Long.parseLong(c.getString(c.getColumnIndex("order_Total"))),
                            c.getString(c.getColumnIndex("order_Date")),
                            c.getString(c.getColumnIndex("order_State")),
                            Float.parseFloat(c.getString(c.getColumnIndex("order_Rating"))),
                            c.getString(c.getColumnIndex("order_Store"))
                    ));
                }while (c.moveToNext() && c != null);
            }
        }else {
            Toast.makeText(ctx, "Unable to initialize cursor", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    public void addToCart(CoffeeOrder coffeeOrder){
        Log.i(TAG,"addToCart()");
        SQLiteDatabase database = getReadableDatabase();
        String insert = "INSERT INTO " +
                " CoffeeOrders (order_Description," +
                "order_CustomerUsername,order_Total,order_Date," +
                "order_State,order_Store,uuid,order_Rating) " +
                "VALUES ('%s','%s','%s','%" +
                "s','%s','%s','%s','%s');";
        String query = String.format(insert,
                                        coffeeOrder.getOrder_Description(),
                                        coffeeOrder.getOrder_CustomerUsername(),
                                        coffeeOrder.getOrder_Total(),
                                        coffeeOrder.getOrder_Date(),
                                        coffeeOrder.getOrder_State(),
                                        coffeeOrder.getOrder_Store(),
                                        coffeeOrder.getUUID(),
                                        coffeeOrder.getOrder_Rating());
        database.execSQL(query);
        
    }

    public void clearCart(){
        Log.i(TAG,"clearCart()");
        SQLiteDatabase database = getReadableDatabase();
        String query= String.format("DELETE from CoffeeOrders");
        database.execSQL(query);
    }
}
