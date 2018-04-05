package prabhu.company.echo18beta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import prabhu.company.echo18beta.misc.Cell;
import prabhu.company.echo18beta.misc.CellMain;

public class TowerFragment extends Fragment {

    //ProgressDialog progress;

    public TowerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    MapView mMapView;
    private GoogleMap googleMap;
    int carriercid = 0, carrierlang = 0, mcc = 0, mnc = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tower, container, false);

        // For showing a move to my location button
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) ;

//        progress = new ProgressDialog(getActivity());
//        progress.setMessage("Loading map...");
//        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progress.setIndeterminate(true);
//        progress.show();

        TelephonyManager Tel = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = null;
        if (Tel != null) {
            cellLocation = (GsmCellLocation) Tel.getCellLocation();
        }
        String networkOperator = null;
        if (Tel != null) {
            networkOperator = Tel.getNetworkOperator();
        }
        if (!TextUtils.isEmpty(networkOperator)) {
            carriercid = cellLocation.getCid();
            carrierlang = cellLocation.getLac() & 0xffff;
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));


            mMapView = rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            displayInMap();
        } else {
            Toast.makeText(getActivity(), "No signal", Toast.LENGTH_SHORT).show();
            //progress.dismiss();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    double lat, longi;

    JSONObject jo2;
    double mlat=0,mlang=0;
    String tokens[]={"9226357cb8dac2","96983ae6ba78d0","904d9acad7f279"};


    @SuppressLint("MissingPermission")
    public void displayInMap() {
        Random rand=new Random();


        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mlat = location.getLatitude();
                    mlang = location.getLongitude();
                }
            }
        });

        String url = "https://ap1.unwiredlabs.com/v2/process.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Gson gson = new Gson();

        CellMain cellmain = new CellMain();

        cellmain.setMcc(mcc);
        cellmain.setMnc(mnc);
        cellmain.setRadio("gsm");
        cellmain.setToken(tokens[(rand.nextInt(2))]);
        cellmain.setAddress(1);
        cellmain.setId(918210281);

        List<Cell> cellList = new ArrayList();
        Cell cell = new Cell();

        cell.setCid(carriercid);
        cell.setLac(carrierlang);
        cell.setPsc(1);

        cellList.add(cell);
        cellmain.setCells(cellList);

        try {
            JsonObject gson2 = new JsonParser().parse(gson.toJson(cellmain)).getAsJsonObject();
            jo2 = new JSONObject(gson2.toString());
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
            Toast.makeText(getActivity(), "Error loading map", Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jo2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("error")) {
                        Toast.makeText(getActivity(), "Sorry there was an error", Toast.LENGTH_SHORT).show();
                    }
                    lat = response.getDouble("lat");
                    longi = response.getDouble("lon");



                    String address = response.getString("address");
                    Log.e("Address",address);


                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            googleMap = mMap;
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            getActivity(), R.raw.map));

                            // For showing a move to my location button
                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE)
                                    != PackageManager.PERMISSION_GRANTED) ;

                            googleMap.setMyLocationEnabled(true);
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                            // For dropping a marker at a point on the Map
                            LatLng sydney = new LatLng(lat, longi);
                            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.signaltower);
                            Bitmap b = bitmapdraw.getBitmap();

                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);



                            MarkerOptions marker = new MarkerOptions().position(sydney).title("Cell tower");
                            marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            googleMap.addMarker(marker);

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(17).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            googleMap.addPolyline(new PolylineOptions()
                                    .add(sydney, new LatLng(mlat, mlang))
                                    .width(10)
                                    .color(Color.RED));

                        }
                    });
                    //progress.dismiss();

                } catch (Exception e) {
                    Toast.makeText(getActivity(),"Error retrieving data",Toast.LENGTH_SHORT).show();
                    Log.e("err",e.toString());
                }
               Log.e("Volley:Response ", "" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley:ERROR ", error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}