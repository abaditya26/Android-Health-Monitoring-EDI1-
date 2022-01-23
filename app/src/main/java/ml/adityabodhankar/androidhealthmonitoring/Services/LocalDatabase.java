package ml.adityabodhankar.androidhealthmonitoring.Services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import ml.adityabodhankar.androidhealthmonitoring.Models.UserModel;

public class LocalDatabase extends SQLiteOpenHelper {
    Context ctx;
    SQLiteDatabase database;
    static final  private String DB_NAME="AndroidHealthMonitoring";
    static final private int DB_VERSION=1;

    static final String DB_USER = "Users";


    public LocalDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+DB_USER);
        db.execSQL("CREATE TABLE IF NOT EXISTS "+DB_USER+" (_id integer primary key autoincrement, " +
                "uId text, name text, email text, phone text, image text, height text, gender text, weight text);");
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

}
