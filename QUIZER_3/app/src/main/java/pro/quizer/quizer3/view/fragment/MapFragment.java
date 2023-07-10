package pro.quizer.quizer3.view.fragment;

import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.LinearRing;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polygon;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.SublayerManager;
import com.yandex.mapkit.mapview.MapView;

import com.google.maps.android.PolyUtil;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;

import pro.quizer.quizer3.adapter.PhonesAdapter;
import pro.quizer.quizer3.database.models.PointR;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.database.models.RouteR;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class MapFragment extends ScreenFragment implements UserLocationObjectListener {

    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private SublayerManager sublayerManager;
    private MapObjectCollection mapObjects;
    private Toolbar mToolbar;
    private Button btnPlus;
    private Button btnMinus;
    private RelativeLayout btnUser;
    private RelativeLayout btnInfo;
    private RelativeLayout btnCenter;
    private RelativeLayout btnBack;
    private RelativeLayout btnExit;
    private HashMap<Integer, List<LatLng>> polygonsMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Point>> pointsMap = new HashMap<>();
    private Polygon polygon;

    private Double centerX = 0.0;
    private Double centerY = 0.0;
    private double zoom = 0.0;
    private AlertDialog infoDialog;
    private MainActivity activity;

    RouteR route;

    List<PointR> pointsR = new ArrayList<>();

    public MapFragment setRoute(RouteR route) {
        this.route = route;
        return this;
    }

    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
//            showToast(point.getLatitude() + ", " + point.getLongitude());
            Log.d("T-A-R", point.getLatitude() + ", " + point.getLongitude());
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
//            Log.d("T-A-R", "onMapLongTap: " + polygonsMap.get(0).size());
//            boolean inside = PolyUtil.containsLocation(new LatLng(point.getLatitude(), point.getLongitude()), polygonsMap.get(0), true);
//
//            showToast(inside + "!");
//            Log.d("T-A-R", inside + " / " + point.getLatitude() + ":" + point.getLongitude());
        }
    };

    public MapFragment() {
        super(R.layout.fragment_map_auto);
    }

    @Override
    protected void onReady() {
        activity = getMainActivity();
        initMap();
        initViews();
        MainFragment.disableSideMenu();

    }

    public void initViews() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_logs_fragment);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Маршруты");
        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));

        btnPlus = findViewById(R.id.plus_btn);
        btnMinus = findViewById(R.id.minus_btn);
        btnUser = findViewById(R.id.user_btn);
        btnInfo = findViewById(R.id.info_btn);
        btnCenter = findViewById(R.id.reset_btn);
        btnBack = findViewById(R.id.back_btn);
        btnExit = findViewById(R.id.exit_btn);

        btnPlus.setOnClickListener(view -> zoomIn());
        btnMinus.setOnClickListener(view -> zoomOut());
        btnUser.setOnClickListener(view -> centerOnUser());
        btnInfo.setOnClickListener(view -> showInfoDialog());
        btnCenter.setOnClickListener(view -> centerOnRoute());
        btnBack.setOnClickListener(view -> replaceFragment(new RoutesFragment()));
        ;
        btnExit.setOnClickListener(view -> replaceFragment(new HomeFragment()));
        ;

        cont.startAnimation(Anim.getAppear(getContext()));
    }

    private void initMap() {
        pointsR = getDao().getPolygon(route.route_id);

        Double maxX = 0.0;
        Double minX = 0.0;
        Double maxY = 0.0;
        Double minY = 0.0;

        if (pointsR.size() > 1) {
            maxX = pointsR.get(0).x;
            minX = pointsR.get(0).x;
            maxY = pointsR.get(0).y;
            minY = pointsR.get(0).y;

            for (PointR point : pointsR) {
                if (point.x > maxX) maxX = point.x;
                if (point.x < minX) minX = point.x;
                if (point.y < minY) minY = point.y;
                if (point.y > maxY) maxY = point.y;

                ArrayList<Point> pointsOnMap = pointsMap.get(point.polygon_number);
                if (pointsOnMap == null) pointsOnMap = new ArrayList<>();

                pointsOnMap.add(new Point(point.x, point.y));
                pointsMap.put(point.polygon_number, pointsOnMap);

//                List<LatLng> polOnMap = polygonsMap.get(point.polygon_number);
//                if (polOnMap == null) polOnMap = new ArrayList<>();
//
//                polOnMap.add(new LatLng(point.x, point.y));
//                polygonsMap.put(point.polygon_number, polOnMap);
            }
        }

        centerX = minX + (maxX - minX) / 2;
        centerY = minY + (maxY - minY) / 2;
        zoom = (45 - (maxY - minY) * 1000) * 0.046 + 13.2;

        MapKitFactory.initialize(getMainActivity());
        mapView = findViewById(R.id.mapview);

        centerOnRoute();

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);

        sublayerManager = mapView.getMap().getSublayerManager();
        mapObjects = mapView.getMap().getMapObjects();

        for (HashMap.Entry<Integer, ArrayList<Point>> entry : pointsMap.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Point> points = entry.getValue();

            polygon = new Polygon(new LinearRing(points), new ArrayList<LinearRing>());
            final PolygonMapObject polygonMapObject = mapObjects.addPolygon(polygon);
            polygonMapObject.setFillColor(0x3300FF00);
            polygonMapObject.setStrokeWidth(4.0f);
            polygonMapObject.setStrokeColor(Color.GREEN);
        }

        mapView.getMap().addInputListener(inputListener);
    }


    @Override
    public boolean onBackPressed() {
        replaceFragment(new RoutesFragment());
        return true;
    }

    @Override
    public void onStop() {
        Log.d("T-A-R", "onStop: <<<<<<<<<<<<<<<<<<");
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

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                activity, R.drawable.man_small));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(activity, R.drawable.man_small),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

//        pinIcon.setIcon(
//                "pin",
//                ImageProvider.fromResource(activity, R.drawable.search_result),
//                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
//                        .setRotationType(RotationType.ROTATE)
//                        .setZIndex(1f)
//                        .setScale(0.5f)
//        );

//        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
    }

    private void centerOnUser() {
        mapView.getMap().move(
                new CameraPosition(userLocationLayer.cameraPosition().getTarget(), 15.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    private void zoomOut() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom() - 1, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    private void zoomIn() {
        mapView.getMap().move(new CameraPosition(mapView.getMap().getCameraPosition().getTarget(),
                        mapView.getMap().getCameraPosition().getZoom() + 1, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }

    private void centerOnRoute() {
        if (!pointsR.isEmpty())
            mapView.getMap().move(
                    new CameraPosition(new Point(centerX, centerY), (float) zoom, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 1),
                    null);
    }

    private void showInfoDialog() {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
            View layoutView = getLayoutInflater().inflate(R.layout.dialog_route_info_auto, null);
            TextView info1 = layoutView.findViewById(R.id.info_1);
            TextView info2 = layoutView.findViewById(R.id.info_2);
            TextView info3 = layoutView.findViewById(R.id.info_3);
            TextView info4 = layoutView.findViewById(R.id.info_4);
            ImageView close = layoutView.findViewById(R.id.btn_dialog_close);

            close.setOnClickListener(v -> {
                try {
                    infoDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            UiUtils.setTextOrHide(info1, (String.format(getString(R.string.route_limit_quiz), String.valueOf(route.route_limit))));
            UiUtils.setTextOrHide(info2, (String.format(getString(R.string.route_rqs_count_all), String.valueOf(route.route_rqs_count_all))));
            UiUtils.setTextOrHide(info3, (String.format(getString(R.string.route_rqs_count_correct_login), String.valueOf(route.route_rqs_count_correct_login))));
            UiUtils.setTextOrHide(info4, (String.format(getString(R.string.route_rqs_count_correct_inter), String.valueOf(route.route_rqs_count_correct_inter))));


            dialogBuilder.setView(layoutView);
            infoDialog = dialogBuilder.create();
            infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
            infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoDialog.setCancelable(false);
            if (activity != null && !activity.isFinishing())
                infoDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

