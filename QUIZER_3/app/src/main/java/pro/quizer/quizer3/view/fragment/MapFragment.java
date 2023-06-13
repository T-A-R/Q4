package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.widget.RelativeLayout;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.ConflictResolutionMode;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.LayerIds;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.Sublayer;
import com.yandex.mapkit.map.SublayerFeatureType;
import com.yandex.mapkit.map.SublayerManager;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.AnimatedImageProvider;

import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.R;

import pro.quizer.quizer3.database.models.PointR;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class MapFragment extends ScreenFragment {

    private MapView mapView;
    private SublayerManager sublayerManager;
    private MapObjectCollection mapObjects;
    private Toolbar mToolbar;
    private List<LatLng> pol = new ArrayList<>();
    private Polygon polygon;

    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            showToast(point.getLatitude() + ", " + point.getLongitude());
            Log.d("T-A-R", point.getLatitude() + ", " + point.getLongitude());
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

            boolean inside = PolyUtil.containsLocation(new LatLng(point.getLatitude(), point.getLongitude()), pol, true);

            showToast(inside + "!");
            Log.d("T-A-R",  inside + " / " + point.getLatitude() + ":" + point.getLongitude());
        }
    };

    public MapFragment() {
        super(R.layout.fragment_map_auto);
    }

    @Override
    protected void onReady() {
        initMap();
        initViews();
        MainFragment.disableSideMenu();

    }

    public void initViews() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_logs_fragment);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Маршруты");
        mToolbar.showCloseView(v -> replaceFragment(new WaypointsFragment()));

        cont.startAnimation(Anim.getAppear(getContext()));
    }

    private void initMap() {
        MapKitFactory.initialize(getMainActivity());
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(
                new CameraPosition(new Point(32.08874604325848, 34.77542656211854), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        sublayerManager = mapView.getMap().getSublayerManager();
        mapObjects = mapView.getMap().getMapObjects();

        List<PointR> polygonR = getDao().getAllPoints();
        ArrayList<Point> points = new ArrayList<>();

        for(PointR point : polygonR) {
            points.add(new Point(point.x, point.y));
            pol.add(new LatLng(point.x, point.y));
        }

        polygon = new Polygon(new LinearRing(points), new ArrayList<LinearRing>());
        final PolygonMapObject polygonMapObject = mapObjects.addPolygon(polygon);
        polygonMapObject.setFillColor(0x3300FF00);
        polygonMapObject.setStrokeWidth(3.0f);
        polygonMapObject.setStrokeColor(Color.GREEN);

        Log.d("", polygon.getInnerRings().contains(new Point(32.08874604325848, 34.77542656211854)) + "!");




        mapView.getMap().addInputListener(inputListener);
    }


    @Override
    public boolean onBackPressed() {
        replaceFragment(new WaypointsFragment());
        return true;
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        super.onStart();
    }
}

