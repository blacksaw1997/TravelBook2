package com.example.erdo.travelbook2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String>names =new ArrayList<>();
    ArrayList<LatLng>location =new ArrayList<>();
    ArrayAdapter arrayAdapter;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.add_place){
            //intent oluşturcaz maps yani
            Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.listView);
        try {
            MapsActivity.database=this.openOrCreateDatabase("Place",MODE_PRIVATE,null);
            Cursor cursor =MapsActivity.database.rawQuery("SELECT * FROM Place",null);

            int nameIx=cursor.getColumnIndex("name");
            int lalitutdeIx=cursor.getColumnIndex("latitude");
            int longtitudeIx=cursor.getColumnIndex("longitude");

            cursor.moveToFirst();

            while(cursor!=null){
                String nameFromDatabese=cursor.getString(nameIx);
                String latitudeFromDatabese=cursor.getString(lalitutdeIx);
                String longFromDatabese=cursor.getString(longtitudeIx);

                names.add(nameFromDatabese);

                Double l1=Double.parseDouble(latitudeFromDatabese);
                Double l2=Double.parseDouble(longFromDatabese);

                LatLng locationFromDatabese= new LatLng(l1,l2);
                location.add(locationFromDatabese);



                cursor.moveToNext();

            }





        }catch (Exception e){
e.printStackTrace();

        }
        arrayAdapter =new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        listView.setAdapter(arrayAdapter);
    }
}
