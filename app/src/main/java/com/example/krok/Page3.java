package com.example.krok;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LogPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.GeometryAdapterFactory;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.gson.GeometryGeoJson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;

public class Page3 extends Fragment
         {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Point> routeCoordinates;
    SampleSQLiteDBHelper db2helper;


private Float CameraLat, CameraLong;
    Switch switch2d3d;
    private String mParam1;
    private String mParam2;
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static MapboxMap mapboxMapst;

    private MapView mapView;
    private OnFragmentInteractionListener mListener;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;



    public Page3() {
    }

    public static Page3 newInstance(String param1, String param2) {
        Page3 fragment = new Page3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db2helper = new SampleSQLiteDBHelper(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        return inflater.inflate(R.layout.page3_layout, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = getView().findViewById(R.id.mapView2D3D);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMapst=mapboxMap;
                mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        initRouteCoordinates();
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(CameraLat, CameraLong))
                                .zoom(14)
                                .tilt(20)
                                .build();
                        mapboxMap.setCameraPosition(position);
// Create the LineString from the list of coordinates and then make a GeoJSON
// FeatureCollection so we can add the line to our map as a layer.
                        style.addSource(new GeoJsonSource("line-source",
                                FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                                        LineString.fromLngLats(routeCoordinates)
                                )})));

// The layer properties for our line. This is where we make the line dotted, set the
// color, etc.
                        style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                          //      PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                PropertyFactory.lineWidth(5f),
                                PropertyFactory.lineOpacity(.7f),
                                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                        ));
                    }
                });
            }
        });

        Switch switchh = getView().findViewById(R.id.switch2d3d);
        switchh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                            mapboxMapst=mapboxMap;
                            mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                }



                            });
                        }
                    });
                }
                else
                {
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                            mapboxMapst=mapboxMap;
                            mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                    initRouteCoordinates();
                                    CameraPosition position = new CameraPosition.Builder()
                                            .target(new LatLng(CameraLat, CameraLong))
                                            .zoom(14)
                                            .tilt(20)
                                            .build();
                                    mapboxMap.setCameraPosition(position);
// Create the LineString from the list of coordinates and then make a GeoJSON
// FeatureCollection so we can add the line to our map as a layer.
                                    style.addSource(new GeoJsonSource("line-source",
                                            FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(
                                                    LineString.fromLngLats(routeCoordinates)
                                            )})));

// The layer properties for our line. This is where we make the line dotted, set the
// color, etc.
                                    style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                                            //      PropertyFactory.lineDasharray(new Float[] {0.01f, 2f}),
                                            PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                            PropertyFactory.lineWidth(5f),
                                            PropertyFactory.lineOpacity(.7f),
                                            PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                                    ));
                                }
                            });
                        }
                    });
                }
            }
        });
    }
/*
             private void initRouteHCoordinates() throws JSONException {
// Create a list to store our line coordinates.
                 routeCoordinates = new ArrayList<>();
                 Geom geometry = new GsonBuilder()
                         .registerTypeAdapterFactory(new GeometryA())
                         .create()
                         .fromJson("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-118.327666667,33.341716667],[-118.327666667,33.341916667],[-118.32786666700001,33.341916667],[-118.32786666700001,33.341716667],[-118.327666667,33.341716667]]]}");


                 JSONObject featureCollection = new JSONObject();
                 featureCollection.put("type", "FeatureCollection");
                 JSONObject crs = new JSONObject();


                     crs.put("type", "name");

                 crs.put("properties", properties);
                 featureCollection.put("crs", crs);

                 JSONArray features = new JSONArray();
                 JSONObject feature = new JSONObject();
                 feature.put("type", "Feature");
                 JSONObject geometry = new JSONObject();

                 JSONAray JSONArrayCoord = new JSONArray();

                 JSONArrayCoord.add(0, 55);
                 JSONArrayCoord.add(1, 55);
                 geometry.put("type", "Point");
                 geometry.put("coordinates", JSONArrayCoord);
                 feature.put("geometry", geometry);

                 features.add(feature);
                 featureCollection.put("features", features);


                 SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                 String dbid = sharedPref.getString("selected_tripID", "empty");


                 Cursor cursor =  db2helper.GetTrip(getContext(), dbid);

                 cursor.moveToFirst();

                 CameraLat = cursor.getFloat(0);
                 CameraLong = cursor.getFloat(1);
                 while ( cursor.moveToNext()){
                     Log.println(Log.ASSERT, "tt", String.valueOf(cursor.getFloat(0)));

                     routeCoordinates.add(Point.fromLngLat(cursor.getFloat(0), cursor.getFloat(1)));

                 }
                 Log.println(Log.ASSERT, "tt", String.valueOf(cursor.isAfterLast()));
             }
*/
    private void initRouteCoordinates() {
// Create a list to store our line coordinates.
        routeCoordinates = new ArrayList<>();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        String dbid = sharedPref.getString("selected_tripID", "empty");


       Cursor cursor =  db2helper.GetTrip(getContext(), dbid);

       cursor.moveToFirst();

       CameraLat = cursor.getFloat(0);
       CameraLong = cursor.getFloat(1);
       while ( cursor.moveToNext()){
           Log.println(Log.ASSERT, "tt", String.valueOf(cursor.getFloat(0)));
           routeCoordinates.add(Point.fromLngLat(cursor.getFloat(0), cursor.getFloat(1)));

       }
        Log.println(Log.ASSERT, "tt", String.valueOf(cursor.isAfterLast()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mapView.onDestroy();

    }





    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

