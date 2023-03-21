package com.example.lab5sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button addButton, readButton, clearButton, deleteButton, updateButton, sortButton;
    EditText nameText, surnameText, phoneText, emailText, addressText, idText;

    RadioGroup radioGroup;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = (RadioGroup) findViewById(R.id.sortRadioGroup);

        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        readButton = (Button) findViewById(R.id.readButton);
        readButton.setOnClickListener(this);
        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);
        sortButton = (Button) findViewById(R.id.sortButton);
        sortButton.setOnClickListener(this);

        idText = (EditText) findViewById(R.id.TextId);
        nameText= (EditText) findViewById(R.id.nameText);
        surnameText = (EditText)  findViewById(R.id.surnameText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        emailText = (EditText) findViewById(R.id.emailText);
        addressText = (EditText) findViewById(R.id.addressText);

        dbHelper = new DatabaseHelper(this);

    }


    @SuppressLint("Range")
    @Override
    public void onClick(View view) {
        String id = idText.getText().toString();
        String name = nameText.getText().toString();
        String surname = surnameText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();
        String address = addressText.getText().toString();

        String orderBy = null;

        Cursor c;

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        switch (view.getId()){
            case R.id.addButton:
                contentValues.put(DatabaseHelper.COLUMN_NAME,name);
                contentValues.put(DatabaseHelper.COLUMN_SURNAME,surname);
                contentValues.put(DatabaseHelper.COLUMN_PHONE,phone);
                contentValues.put(DatabaseHelper.COLUMN_EMAIL,email);
                contentValues.put(DatabaseHelper.COLUMN_ADDRESS,address);

                database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                break;
            case R.id.readButton:
                Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,null,null,null,null,null,null);

                if(cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
                    int surnameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SURNAME);
                    int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE);
                    int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
                    int addressIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS);
                        do {
                            Log.d("mLog", "ID = " + cursor.getInt(idIndex)+
                                    ", name = " + cursor.getString(nameIndex)+
                                    ", surname = " + cursor.getString(surnameIndex)+
                                    ", phone = " + cursor.getString(phoneIndex)+
                                    ", email = " + cursor.getString(emailIndex)+
                                    ", address = " + cursor.getString(addressIndex));
                        }while (cursor.moveToNext());
                }else {
                    Log.d("mLog","0 rows");
                }
                cursor.close();
                break;
            case R.id.clearButton:
                database.delete(DatabaseHelper.TABLE_NAME,null, null);
                break;
            case R.id.deleteButton:
                if(id.equalsIgnoreCase("")){
                    break;
                }
                int delCount = database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "= " + id, null);
                Log.d("mLog", "deleted tows count = " + delCount);
                break;
            case R.id.updateButton:
                if(id.equalsIgnoreCase("")){
                    break;
                }
                contentValues.put(DatabaseHelper.COLUMN_NAME,name);
                contentValues.put(DatabaseHelper.COLUMN_SURNAME,surname);
                contentValues.put(DatabaseHelper.COLUMN_PHONE,phone);
                contentValues.put(DatabaseHelper.COLUMN_EMAIL,email);
                contentValues.put(DatabaseHelper.COLUMN_ADDRESS,address);

                int updCount = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.COLUMN_ID + "= ?", new String[] {id});

                Log.d("mLog", "updates rows count = " + updCount);
                break;
            case R.id.sortButton:
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.nameRadio:
                        Log.d("mLog", "Сортування за іменем");
                        orderBy = "name";
                        break;
                    case R.id.surnameRadio:
                        Log.d("mLog", "Сортування за прізвищем");
                        orderBy = "surname";
                        break;
                    case R.id.emailRadio:
                        Log.d("mLog", "Сортування за поштою");
                        orderBy = "email";
                        break;
                }
                c = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, orderBy);

                if (c!=null){
                    if(c.moveToFirst()){
                        String str;
                        do{
                            str="";
                            for(String cn : c.getColumnNames()){
                                str = str.concat(cn + " = "
                                + c.getString(c.getColumnIndex(cn)) + ";");
                            }
                            Log.d("mLog",str);
                        }while (c.moveToNext());
                    }
                    c.close();
                }else {
                    Log.d("mLog","Cursor is null");
                }
                break;
        }
        dbHelper.close();
    }
}