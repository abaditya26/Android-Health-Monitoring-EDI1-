package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ml.adityabodhankar.androidhealthmonitoring.Models.ModelGoal;
import ml.adityabodhankar.androidhealthmonitoring.Models.StepModel;
import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class LocalDatabase extends SQLiteOpenHelper {
    Context ctx;
    SQLiteDatabase database;
    static final  private String DB_NAME="AndroidHealthMonitoring";
    static final private int DB_VERSION=1;

    static final String DB_USER = "Users";
    static final private String DB_STEPS="Steps";
    static final private String DB_GOAL="Goals";


    public LocalDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DB_USER);
        db.execSQL("DROP TABLE IF EXISTS "+DB_STEPS);
        db.execSQL("DROP TABLE IF EXISTS "+DB_GOAL);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DB_USER+" (_id integer primary key autoincrement, " +
                "uId text, name text, email text, phone text, image text, height text, gender text, weight text);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DB_STEPS+" (_id integer primary key autoincrement, " +
                "steps text, uId text, date text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DB_GOAL+" (_id integer primary key autoincrement, " +
                "uId text, stepGoal text, caloriesGoal text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }

    public void createUser(UserModel user) {
        SQLiteDatabase db = getWritableDatabase();
        onCreate(db);
        setUserData(user);
    }

    public void setUserData(UserModel user){
        database=getReadableDatabase();
        Cursor cr = database.rawQuery("SELECT COUNT(*) FROM "+DB_USER+" WHERE uId='"+user.getUid()+"'",null);
        cr.moveToNext();
        database=getWritableDatabase();
        if (cr.getString(0).equalsIgnoreCase("1")){
            database.execSQL("UPDATE "+DB_USER+" SET name = '"+user.getName()+"', email ='"+user.getEmail()+"', " +
                    "phone ='"+user.getPhone()+"', height ='"+user.getHeight()+"', weight= '"+user.getWeight()+"', gender ='"+user.getGender()+"', " +
                    "image ='"+user.getImage()+"' WHERE uId = '"+user.getUid()+"';");
        }else{
            database.execSQL("INSERT INTO USERS(uId, name, email, phone, height, weight, gender, image) " +
                    "VALUES('"+user.getUid()+"', '"+user.getName()+"', '"+user.getEmail()+"', '"+user.getPhone()+"', " +
                    "'"+user.getHeight()+"', '"+user.getWeight()+"', '"+user.getGender()+"', '"+user.getImage()+"');");
        }
        cr.close();
    }

    public UserModel getUser(String uId){
        UserModel user = new UserModel();
        database = getReadableDatabase();
        Cursor cr = database.rawQuery("SELECT * FROM "+DB_USER+" WHERE uId = '"+uId+"'", null);
        while (cr.moveToNext()){
            try {
                user.setUid(uId);
                user.setName(cr.getString(2));
                user.setEmail(cr.getString(3));
                user.setPhone(cr.getString(4));
                user.setImage(cr.getString(5));
                user.setHeight(cr.getString(6));
                user.setGender(cr.getString(7));
                user.setWeight(cr.getString(8));
            }catch (Exception e){
                Toast.makeText(ctx, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        cr.close();
        return user;
    }

    public void insertSteps(String steps, String uId){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = formatter.format(date);

        database=getReadableDatabase();

        Cursor cr =database.rawQuery("SELECT COUNT(*) FROM "+DB_STEPS+" WHERE uId = '"+uId+"' AND date = '"+today+"';",null);
        cr.moveToNext();

        database=getWritableDatabase();

        if (cr.getString(0).equals("1")){
            database.execSQL("UPDATE "+DB_STEPS+" SET steps = '"+steps+"' WHERE uId='"+uId+"' AND date ='"+today+"';");
        }else {
            database.execSQL("INSERT INTO " + DB_STEPS + "(steps, uId, date) VALUES('" + steps + "','" + uId + "','" + today + "');");
        }
        cr.close();
    }

    public String getSteps(String uId, String date){

        database=getReadableDatabase();
        Cursor cr =database.rawQuery("SELECT * FROM "+DB_STEPS+" WHERE uId = '"+uId+"' AND date = '"+date+"';",null);

        String step = "0";
        while (cr.moveToNext()){
            try{
                step =cr.getString(1);
            }catch (Exception e){
                Toast.makeText(ctx, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        cr.close();
        return step;
    }

    public void insertGoal(String uId, String steps, String calorie){
        database=getReadableDatabase();
        Cursor cr =database.rawQuery("SELECT COUNT(*) FROM "+DB_GOAL+" WHERE uId = '"+uId+"';",null);
        cr.moveToNext();
        database=getWritableDatabase();
        if (cr.getString(0).equals("1")){
            database.execSQL("UPDATE "+DB_GOAL+" SET stepGoal='"+steps+"', caloriesGoal ='"+calorie+"';");
        }else {
            database.execSQL("INSERT INTO "+DB_GOAL+"(uId, stepGoal, caloriesGoal) VALUES('"+uId+"','"+steps+"','"+calorie+"');");
        }
        cr.close();
    }

    public ModelGoal getGoal(String uId){
        database=getReadableDatabase();
        Cursor cr =database.rawQuery("SELECT * FROM "+DB_GOAL+" WHERE uId = '"+uId+"';",null);
        String step = "500",calorie = "100";
        boolean isDataAvailable = false;
        while (cr.moveToNext()) {
            step = cr.getString(2);
            calorie = cr.getString(3);
            isDataAvailable = true;
        }
        if (!isDataAvailable){
            insertGoal(uId, step, calorie);
        }
        cr.close();
        return new ModelGoal(step,calorie);
    }


    public List<StepModel> getOldSteps(String uId, boolean flag){
        List<StepModel> stepsList = new ArrayList<>();

        database=getReadableDatabase();
        Cursor cr =database.rawQuery("SELECT * FROM "+DB_STEPS+" WHERE uId = '"+uId+"' ORDER BY date DESC;",null);
        int count = 0;
        while (cr.moveToNext()){
            try{
                String step =cr.getString(1);
                String date = cr.getString(3);
                stepsList.add(new StepModel(date,step,uId));
            }catch (Exception e){
                Toast.makeText(ctx, "Error => "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            count++;
            if(!flag) {
                if (count >= 5) {
                    break;
                }
            }
        }

        cr.close();
        return stepsList;
    }

    public void addOldSteps(List<StepModel> stepsList, String uId) {
        database = getReadableDatabase();
        for (StepModel stepsModel : stepsList){
            Cursor cr =database.rawQuery("SELECT COUNT(*) FROM "+DB_STEPS+" WHERE uId = '"+uId+"' AND date = '"+stepsModel.getDate()+"';",null);
            cr.moveToNext();

            database=getWritableDatabase();

            if (cr.getString(0).equals("1")){
                database.execSQL("UPDATE "+DB_STEPS+" SET steps = '"+ stepsModel.getSteps()+"' WHERE uId='"+uId+"' AND date ='"+stepsModel.getDate()+"';");
            }else {
                database.execSQL("INSERT INTO " + DB_STEPS + "(steps, uId, date) VALUES('" + stepsModel.getSteps() + "','" + uId + "','" + stepsModel.getDate() + "');");
            }
            cr.close();
        }
    }

}
