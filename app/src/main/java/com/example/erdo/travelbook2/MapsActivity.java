package com.example.erdo.travelbook2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);// haritayı bağladık
locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
locationListener=new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
        LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
};

//kullanuıcı izinleri
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


        }else{

           try {
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
               mMap.clear();
               Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               LatLng lastUserLocation =new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));


           }catch (Exception e)
           {
               e.printStackTrace();
           }




        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0){//ilk defa izin verdiğinde

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                mMap.clear();
                Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastUserLocation =new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));

            }
        }



    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    //geocoder olusup adresi alcaz reverse etcez.
        Geocoder geocoder =new Geocoder(getApplicationContext(), Locale.getDefault());
        String adress="";

        try {
            List<Address> addressList =geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList!=null && addressList.size()>0){
                if (addressList.get(0).getThoroughfare()!=null){ //thougrhfare addres aldı sokak isimlerini
                    adress +=addressList.get(0).getThoroughfare();
                    if (addressList.get(0).getThoroughfare()!=null){
                        adress +=addressList.get(0).getSubThoroughfare();


                    }
                }else{
                    adress="New Place";
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(adress));
        Toast.makeText(getApplicationContext(),"Yeni yer oluşturuldu",Toast.LENGTH_LONG).show();

        try {
            Double l1=latLng.latitude;
            Double l2=latLng.longitude;
            String kor1=l1.toString();
            String kor2=l2.toString();

            database =this.openOrCreateDatabase("Place",MODE_PRIVATE,null);

            String tablo ="CREATE TABLE IF NOT EXISTS Place(name VARCHAR, latitude VARCHAR, longitute VARCHAR)";
            database.execSQL(tablo);



            String toCompile="INSERT INTO place(name,latitude,longitute) VALUES (?, ?, ?)";
            SQLiteStatement sqLiteStatement =database.compileStatement(toCompile);
            sqLiteStatement.bindString(1,adress);
            sqLiteStatement.bindString(2,kor1);
            sqLiteStatement.bindString(3,kor2);
            sqLiteStatement.execute();






        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
