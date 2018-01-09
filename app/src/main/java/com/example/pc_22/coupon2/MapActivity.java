package com.example.pc_22.coupon2;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements MapView.CurrentLocationEventListener, MapView.POIItemEventListener {

    static final int REQUEST_COUPONLIST = 10;
    String userid;
TextView txt_userid;
    FirebaseFirestore db;
Boolean inok = true;
    static MapView mapView;
    MapPolyline polyline;
Boolean isdrawLine = false;
    static double targetCenter1 = 0;
    static  double targetCenter2 = 0;
    static String targetName = null;
static String targetCouponName = null;
    static String targetSelectName =null;
Boolean isResume = false;
    ArrayList<tolocation> arr = new ArrayList<>();
    ArrayList<String> arr22;
    HashMap<String, Object> hm = new HashMap<>();
    private Toast toast;
    CountDownTimer timer;
    int mytimer=0;
    int cnt = 0;
    Boolean searchOn = false;

    TextView tvm1;
    TextView tvm2;
    TextView tvm3;
    TextView tvm4;
    TextView tvm5;
    TextView tvm6;

    TextView tv_info;
    TextView tv_infoview;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*파베삭제처리는 차곡차곡해야된다 젤큰거하나지워도 찌꺼기 그대로남아있더라.*/
        db.collection("CurrentUserPosition").document(userid+"").collection("Coupon").document("target").delete();
        db.collection("CurrentUserPosition").document(userid+"").delete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isResume){
                mapView.removeAllPolylines();
                targetCenter2=0;
                targetCenter1=0;
                targetName=null;
                targetCouponName=null;
                isdrawLine=false;
                isResume=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        db = FirebaseFirestore.getInstance();
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_infoview = (TextView) findViewById(R.id.tv_infoview);
        tvm1 = (TextView) findViewById(R.id.tvm1);

        Intent intent=new Intent(this.getIntent());
        userid=intent.getStringExtra("userid");

        tvm1.setText("숨겨진 쿠폰 탐지 : OFF");
        tvm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str1 = "숨겨진 쿠폰 탐지 : ON";
                String str2 = "숨겨진 쿠폰 탐지 : OFF";
                if(tvm1.getText().equals(str1)){
                    tvm1.setText(str2);
                    searchOn = false;
                }else{
                    tvm1.setText(str1);
                    tv_infoview.setText("탐지ON : 숨겨진 쿠폰을 찾는 중 입니다..!");
                    searchOn = true;
                }
            }
        });
        tvm2 = (TextView) findViewById(R.id.tvm2);
        tvm2.setText(userid + "님 환영합니다!");
        tvm3 = (TextView) findViewById(R.id.tvm3);
        tvm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*여기에다가 추가하면됨 ㅇㅇ 지호 지호 지호 지호 */
                inok=false;
                Intent intent = new Intent(getApplicationContext(), sepActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
                inok=true;
            }
        });
        tvm4 = (TextView) findViewById(R.id.tvm4);
        tvm4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inok=false;
                Intent intent = new Intent(getApplicationContext(), sepActivity.class);
                intent.putExtra("userid",userid);
                startActivity(intent);
                inok=true;
            }
        });
        tvm5 = (TextView) findViewById(R.id.tvm5);
        tvm5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inok=false;
                Intent intent = new Intent(getApplicationContext(), sepActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("isok",true);
                startActivity(intent);
                inok=true;
            }
        });
        tvm6 = (TextView) findViewById(R.id.tvm6);
        tvm6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*병우쓰면됨*/
                inok=false;
                Intent intent = new Intent(getApplicationContext(), sepActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("isok",true);
                startActivity(intent);
                inok=true;
            }
        });

        tv_infoview.setText(userid +" 님 어서오세요. 쿠폰탐지OFF");
/*        txt_userid = (TextView)findViewById(R.id.txt_userid);
        txt_userid.setText(userid + "님 접속을 환영합니다.");*/

        mapView = new MapView(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord( 35.110934161909555, 126.87715655865162), true);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        db.collection("CouponPlace")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        MapPoint DEFAULT_MARKER_POINT;
                        int i = 0;
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord((double)document.getData().get("Lat")+0, (double)document.getData().get("Lng")+0);
                                MapPOIItem marker = new MapPOIItem();
                                marker.setItemName(document.getData().get("Name")+"");
                                marker.setTag(0);
                                marker.setMapPoint(DEFAULT_MARKER_POINT);
                                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                                mapView.addPOIItem(marker);
                            }

                        } else {

                        }
                    }
                });

      arr22 = new ArrayList<>();

/*투명쿠폰불러오기*/
        db.collection("CouponPlace")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String str = (String)document.getData().get("Name")+"";
                                db.collection("CouponPlace").document(str+"").collection("Coupon")
                                        .whereEqualTo("Cis", true)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            String pcname = document.getData().get("Cname") + "";
                                                            String place = document.getData().get("Cplace") + "";
                                                            double pclat = (double) document.getData().get("Clat");
                                                            double pclng = (double) document.getData().get("Clng");
                                                            arr.add(new tolocation(pcname, place, pclat, pclng));
                                                            Log.d("zzzzz", arr.get(0).getPcname()+"");
                                                        }
                                                    }

                                            }
                                        });
                            }
                        } else {

                        }
                    }
                });

/*
        */
/*투명로케이션용 이벤트리스너등록 미사용*//*

        db.collection("CurrentUserPosition").document(userid+"").collection("Coupon").document("target").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("errer", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(targetName.equals(snapshot.getData().get("Cplace")+"") && targetCouponName.equals(arr22.add(snapshot.getData().get("Cname")+""))){

                    }
                    targetCouponName = snapshot.getData().get("targetName")+"";
                    arr22.add(snapshot.getData().get("Cplace")+"");
                    arr22.add(snapshot.getData().get("Cname")+"");
                    arr22.add(snapshot.getData().get("userid")+"");

                    isResume = true;
                    onResume();
                } else {

                }
            }
        });

*/


    }

    private void callCoupon() {
    /*메소드화*//*쿠폰위치감지성공시호출*/
        if(targetName!=null && targetCouponName != null){
            if(timer != null){
                timer.cancel();
            }
            timer=null;
            tv_infoview.setText("쿠폰 발급 절차를 진행중 입니다.");
            db.collection("CouponPlace").document(targetName+"").collection("Coupon").document(targetCouponName+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            String strDate = (String)document.get("Ctime");

                            try {
                                Date date = dateFormat.parse(strDate);
                                long now = System.currentTimeMillis();
                                Date date2 = new Date(now);
                                if(date2.before(date)){

                                    /*쿠폰의 데이터를불러와 쿠폰의 유효기간이 유효할경우 쿠폰발급창으로 이동하자.*/
                                    db.collection("CouponPlace").document(targetName+"").collection("Coupon").document(targetCouponName+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        int cquantity = 0;
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    String str =  document.get("Cquantity")+"";
                                                    cquantity = Integer.parseInt(str);
                                                    if(cquantity > 0) {
                                                        tv_infoview.setText("쿠폰인증 OK");
                                      /* 쿠폰의 발급처리를 추가할것*/
                                                        Map<String, Object> icoupon =new HashMap<>();
                                                        icoupon.put("Cname", document.get("Cname")+"");
                                                        icoupon.put("Ccon", document.get("Ccon")+"");
                                                        icoupon.put("Cplace", document.get("Cplace")+"");
                                                        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                                                        String strDate = (String)document.get("Ctime");
                                                        icoupon.put("Ctime", strDate);
                                                        icoupon.put("Cquantity", cquantity);
                                                        icoupon.put("userid", userid);
                                                        icoupon.put("targetName", targetName);
                                                        icoupon.put("targetCouponName", targetCouponName);
                                                        settouch(true);
                                                        isResume=true;
                                                        Intent intent = new Intent(getApplicationContext(), giftcouponActivity.class);
                                                        intent.putExtra("coupon", (Serializable) icoupon);
                                                        startActivity(intent);
                                                        settouch(false);
                                                    }else{
                                                        Toast.makeText(getApplicationContext(), "쿠폰수량소진, 발급불가! "+cquantity, Toast.LENGTH_SHORT).show();
                                                        tv_infoview.setText("죄송해요! 수량이 모두 소진된 쿠폰입니다.");
                                                    }
                                                }
                                            }
                                        }
                                    });

                                }else{
                                    tv_infoview.setText("죄송합니다. 쿠폰의 유효기간을 확인해주세요.");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "쿠폰데이터가없음", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "쿠폰데이터가없음", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }else{

        }
    }

    private void drawLineArea(MapView mapView,double center1, double center2) {

        polyline = new MapPolyline();
        mapView.removePolyline(polyline);
        mapView.removeAllPolylines();

        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

        double tempLat1 = center1+0.0001;
        double tempLng1 = center2-0.0002;
// Polyline 좌표 지정.
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(center1+0.0001, center2-0.0002));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(tempLat1-0.0004,tempLng1+0));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(tempLat1-0.0004,tempLng1+0.0004));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(tempLat1+0,tempLng1+0.0004));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(center1+0.0001, center2-0.0002));
// Polyline 지도에 올리기.
        mapView.addPolyline(polyline);
        isdrawLine = true;
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
       /* Log.i("maptest", String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, v));*/

        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);

        Map<String, Object> curP = new HashMap<>();
        curP.put("systime",strDate);
        curP.put("Currentlat", mapPointGeo.latitude);
        curP.put("Currentlng", mapPointGeo.longitude);
/*        GeoPoint gp = new GeoPoint(mapPointGeo.latitude,mapPointGeo.longitude);
        curP.put("gp",gp);*/
        db.collection("CurrentUserPosition").document(userid+"").set(curP);
        if(targetCouponName!=null && targetName != null){
            tv_infoview.setText(targetName + "의" + targetCouponName + " 쿠폰을 추적중 입니다.");
        }else if(userid != null){
            tv_infoview.setText(userid +"님 새로운 쿠폰을 설정해보세요");
        }
        if(inok){
            if(targetCenter1 != 0 && targetCenter2 != 0) {
                DetectionArea(mapPointGeo);
            }else if(searchOn){
                DetectionArea2(mapPointGeo);
            }
        }

    }

    private void DetectionArea2(MapPoint.GeoCoordinate mapPointGeo) {


        for (int i=0; i<arr.size(); i++) {
            if (inok = true) {
                double tempcenter1 = arr.get(i).getPclat();
                double tempcenter2 = arr.get(i).getPclng();

                Log.d("hdfhdf", arr.get(i).getPclat() + " " + i);
                Log.d("hdfhdf", arr.get(i).getPclng() + " " + i);
                Log.d("hdfhdf", arr.get(i).getPcname() + " " + i);
                double tempLat1 = 35.11111;
                double tempLng1 = 126.87751;
                tempLat1 = tempcenter1 + 0.0001;
                tempLng1 = tempcenter2 - 0.0002;


                if (tempLat1 >= mapPointGeo.latitude && (tempLat1 - 0.0004) <= mapPointGeo.latitude && tempLng1 <= mapPointGeo.longitude && (tempLng1 + 0.0004) >= mapPointGeo.longitude) {
                    tv_infoview.setText("축하합니다!! 숨겨진 쿠폰을 발견했습니다!!");
                /*쿠폰조회*/
                    final int finalI = i;
                    db.collection("CouponPlace").document(arr.get(i).getPlace() + "").collection("Coupon").document(arr.get(i).getPcname() + "").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                       /*쿠폰을 가지고있는지 체크*/
                                    Log.d("hdfhdf", document.getData().get("Cname") + "");
                                    Log.d("hdfhdf", document.getData().get("Cplace") + "");
                                    db.collection("myuser").document(userid + "").collection("Coupon")
                                            .whereEqualTo("Cname", document.getData().get("Cname") + "")
                                            .whereEqualTo("Cplace", document.getData().get("Cplace") + "")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    Log.d("hdfhdf", task.getResult().size() + " ");
                                                    if (task.getResult().isEmpty()) {
                                                        if(inok){
                                                            inok = false;
                                                            settouch(true);
                                                            Intent intent = new Intent(getApplicationContext(), TempActivity.class);
                                                            intent.putExtra("targetName", arr.get(finalI).getPlace() + "");
                                                            intent.putExtra("userid", userid + "");
                                                            intent.putExtra("isok", true);
                                                            startActivityForResult(intent, REQUEST_COUPONLIST);
                                                        }

                                                  /*  callCoupon(); */
                                                    } else {
                                                    }
                                                }
                                            });

                                } else {
                                    Log.d("error", "No such document");
                                }
                            } else {
                                Log.d("error", "get failed with ", task.getException());
                            }
                        }
                    });
                }

            }
        }
    }

    private void DetectionArea(MapPoint.GeoCoordinate mapPointGeo) {


        double tempcenter1 = targetCenter1;
        double tempcenter2 = targetCenter2;
        double tempLat1 = 35.11111;
        double tempLng1 = 126.87751;
/*0.0001 + 0.0002 - */
        /* 첫점을 구한다. 대각선 좌측 상단점 = 센터의 위도 + 0.0001 , 경도는 -0.0002*/
        /* 좌측하단점 40m 기준으로 0.0004값을 - , 경도값변함없음*/
        /* 좌측하단점에서 우측하단점 = 경도는 0.0004값을+ */

        tempLat1 = tempcenter1 + 0.0001;
        tempLng1 = tempcenter2 - 0.0002;





        if (tempLat1 >= mapPointGeo.latitude && (tempLat1 - 0.0004) <= mapPointGeo.latitude && tempLng1 <= mapPointGeo.longitude && (tempLng1 + 0.0004) >= mapPointGeo.longitude) {
            inok = false;

            if (targetName != null) {
                tv_infoview.setText("쿠폰 발급 장소 진입을 확인했습니다.");
                /*쿠폰조회*/
                db.collection("CouponPlace").document(targetName + "").collection("Coupon").document(targetCouponName+"").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                       /*쿠폰을 가지고있는지 체크*/
                                db.collection("myuser").document(userid + "").collection("Coupon")
                                        .whereEqualTo("Cname", document.getData().get("Cname")+"")
                                        .whereEqualTo("Cplace", document.getData().get("Cplace")+"")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.getResult().isEmpty()) {
                                                    callCoupon();
                                                } else {
                                                    tv_infoview.setText("발급실패, 이미 보유한 쿠폰입니다.");
                                                }
                                            }
                                        });

                            } else {
                                Log.d("error", "No such document");
                            }
                        } else {
                            Log.d("error", "get failed with ", task.getException());
                        }
                    }
                });


            }
        }

    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }


    @Override
    public void onPOIItemSelected(final MapView mapView, MapPOIItem mapPOIItem) {
        targetSelectName = mapPOIItem.getItemName()+"";
        if(isdrawLine && targetSelectName.equals(mapPOIItem.getItemName()+"")){
            mapView.removeAllPolylines();
            targetCenter2=0;
            targetCenter1=0;
            targetName=null;
            targetCouponName=null;
            isdrawLine = false;
            if(timer != null){
                timer.cancel();
            }
            timer=null;
            tv_infoview.setText("마커를 터치해 새로운 쿠폰 장소를 설정해보세요!");
        }else if (isdrawLine){
            mapView.removeAllPolylines();
            targetCenter2=0;
            targetCenter1=0;
            targetName=null;
            targetCouponName=null;
            isdrawLine = false;
            if(timer != null){
                timer.cancel();
            }
            timer=null;
            tv_infoview.setText("마커를 터치해 새로운 쿠폰 장소를 설정해보세요!");
        }else {
            db.collection("CouponPlace").document(targetSelectName + "").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            targetCenter1 = (double) document.getData().get("Lat");
                            targetCenter2 = (double) document.getData().get("Lng");
                            targetName = (String) document.getData().get("Name");

                            tv_infoview.setText("현재 설정된 장소는 " +targetName+" 입니다.");
                            Intent intent = new Intent(getApplicationContext(), TempActivity.class);
                            intent.putExtra("targetName",targetName+"");
                            intent.putExtra("userid",userid+"");
                            intent.putExtra("isok",false);
                            startActivityForResult(intent,REQUEST_COUPONLIST);
                            drawLineArea(mapView, targetCenter1, targetCenter2);

                        } else {

                        }
                    } else {
                        Log.d("d", "get failed with ", task.getException());
                    }
                }
            });
        }
    }
public void settimer(int time){
    long mil = time * 1000;
    toast = Toast.makeText(getApplicationContext(),time +"초 남았습니다!",Toast.LENGTH_SHORT);
    toast.show();

    timer = new CountDownTimer(mil+0, 1000) {
        @Override
        public void onTick(long l) {
            toast.cancel();
            cnt = (int)l/1000;
            toast = Toast.makeText(getApplicationContext(),cnt +"초 남았습니다!",Toast.LENGTH_SHORT);
            toast.show();

        }
        @Override
        public void onFinish() {
            toast.cancel();
            toast = Toast.makeText(getApplicationContext(),"시간초과.. 획득에 실패했습니다..",Toast.LENGTH_SHORT);
            tv_infoview.setText("시간초과로 인해 쿠폰획득에 실패했어요..");
            toast.show();
                mapView.removeAllPolylines();
                targetCenter2=0;
                targetCenter1=0;
                targetName=null;
                targetCouponName=null;
                isdrawLine = false;
                if(timer != null){
                    timer.cancel();
                  }
                timer=null;


        }
    };
    timer.start();
}
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_COUPONLIST:
                if(resultCode == RESULT_OK){
                    targetCouponName = data.getStringExtra("CouponName")+"";
                    targetName = data.getStringExtra("CouponPlace")+"";
                    mytimer = data.getIntExtra("mytimer",0);

                    if(targetCouponName == null){
                        mapView.removeAllPolylines();
                        targetCenter2=0;
                        targetCenter1=0;
                        targetName=null;
                        targetCouponName=null;
                        isdrawLine = false;
                    }else{
                        db.collection("CouponPlace").document(targetName + "").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        targetCenter1 = (double) document.getData().get("Lat");
                                        targetCenter2 = (double) document.getData().get("Lng");
                                        targetName = (String) document.getData().get("Name");
                                        if(targetName!=null){
                                            tv_infoview.setText(targetName+"으로 이동을 시작해주세요.");
                                        }
                                        Log.d("getReturnCoupon", targetCenter1+" "+targetCenter2+" "+targetName+" "+targetCouponName);
                                        drawLineArea(mapView, targetCenter1, targetCenter2);
                                        isdrawLine = true;

                                        if(mytimer > 0){
                                            settimer(mytimer);
                                        }
                                    } else {

                                    }
                                } else {
                                    Log.d("d", "get failed with ", task.getException());
                                }
                            }
                        });
                    }

                    settouch(false);
                    inok=true;
                }else{
                    if(targetCouponName == null){
                        mapView.removeAllPolylines();
                        targetCenter2=0;
                        targetCenter1=0;
                        targetName=null;
                        targetCouponName=null;
                        isdrawLine = false;
                    }
                    targetCouponName=null;
                    settouch(false);
                    inok=true;
                }
                break;
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public void settouch(Boolean k){
        if(k){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
