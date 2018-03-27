package prabhu.company.echo18beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CoverageFragment extends Fragment {

    public WebView mWebView;
    public String url = "https://10.0.0.236:8080";
    //public String url = "https://www.google.com/";
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_coverage, container, false);
        mWebView = v.findViewById(R.id.webview);
        mWebView.loadUrl(url);
        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        mWebView.getSettings().setGeolocationDatabasePath(getContext().getDatabasePath("Geo-Locations").getAbsolutePath());
        return v;
    }

    //ProgressDialog progress;
/*
    public CoverageFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_coverage, container, false);

        // For showing a move to my location button
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) ;

        TelephonyManager Tel = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation) Tel.getCellLocation();
        String networkOperator = Tel.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            carriercid = cellLocation.getCid();
            carrierlang = cellLocation.getLac() & 0xffff;
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));


            mMapView = (MapView) rootView.findViewById(R.id.coverageMap);
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
    TileOverlay mOverlay;
    private FusedLocationProviderClient fusedLocationProviderClient;
    JSONObject jo2;
    double mlat=0,mlang=0;
    String tokens[]={"9226357cb8dac2","96983ae6ba78d0","904d9acad7f279"};

    @SuppressLint("MissingPermission")
    public void displayInMap() {
        Random rand=new Random();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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

//                            int height = 100;
//                            int width = 100;
//                            /BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.signaltower);
//                            Bitmap b = bitmapdraw.getBitmap();
//
//                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//
//
//
//                            MarkerOptions marker = new MarkerOptions().position(sydney).title("Cell tower");
//                            marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//                            googleMap.addMarker(marker);


                            List<LatLng> list = null;

                            // Get the data: latitude/longitude positions of police stations.
                            try {
                                list = readItems(R.raw.police);
                                Log.e("DANCCEMAMU",list.toString());
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Problem reading list of locations.", Toast.LENGTH_LONG).show();
                                Log.e("DANCCEMAMU","FAILMAMU"+e.toString());
                            }


                            int[] colors = {
                                    Color.rgb(102, 225, 0), // green
                                    Color.rgb(255, 0, 0)    // red
                            };

                            float[] startPoints = {
                                    0.2f, 1f
                            };

                            Gradient gradient = new Gradient(colors, startPoints);

// Create the tile provider.
                            // Create a heat map tile provider, passing it the latlngs of the police stations.
                            HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                    .data(list)
                                    .gradient(gradient)
                                    .build();
                            // Add a tile overlay to the map, using the heat map tile provider.
                            mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));


//                            Polyline line = googleMap.addPolyline(new PolylineOptions()
//                                    .add(sydney, new LatLng(mlat, mlang))
//                                    .width(10)
//                                    .color(Color.RED));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(17).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                Log.e("Volley:ERROR ", error.getMessage().toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }
    */
}