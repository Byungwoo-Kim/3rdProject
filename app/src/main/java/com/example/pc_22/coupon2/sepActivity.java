package com.example.pc_22.coupon2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class sepActivity extends AppCompatActivity {


    String userid;
    FirebaseFirestore db;
    String str = "";
    Boolean isresume = false;
    Map<String, Object> data = new HashMap<>();


    ListView listview;
    ListViewAdapter_my adapter;
    ArrayList<clist> arr;
    Boolean isok = false;

    String[] dataArray = null;

    @Override
    protected void onResume() {
        super.onResume();

        if (isresume) {
            db.collection("myuser").document(userid + "").collection("Coupon")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            MapPoint DEFAULT_MARKER_POINT;
                            int i = 0;
                            if (task.isSuccessful()) {
                                if (task.getResult().size() <= 1) {
                                    str = "현재 사용가능한 쿠폰이 없어요.";
                                } else {
                                    arr.clear();
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String name = document.getData().get("Cname") + "";
                                        if (!name.equals("") || name != null) {
                                            String con = document.getData().get("Ccon") + "";
                                            String place = document.getData().get("Cplace") + "";
                                            String time = document.getData().get("Ctime") + "";
                                            arr.add(new clist(name, con, time, place));
                                        }
                                    }
                                    makelist();
                                    isresume = false;
                                }
                            } else {
                            }
                        }
                    });
        }
    }

    private void makelist() {
        if (isok && arr.size() == 0) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                    "아직 이용 기록이 없어요!", "사용한 쿠폰이 기록이 없어요.", "", "");
        } else if (isok == false && arr.size() <= 1) {
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                    "쿠폰을 발급받아봅시다!", "사용가능한쿠폰이없어요.", "", "");
        } else {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getName().equals("")) {
                    continue;
                } else if (arr.get(i).getName() == null) {
                    continue;
                }
                if (isok) {
                    adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                            arr.get(i).getName() + "", arr.get(i).getCon() + "", arr.get(i).getTime() + "에 사용됨", arr.get(i).getPlace() + "");
                    Log.d("ririri", arr.get(i).getName() + "");
                } else {
                    adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                            arr.get(i).getName() + "", arr.get(i).getCon() + "", arr.get(i).getTime() + "까지", arr.get(i).getPlace() + "");
                    Log.d("ririri", arr.get(i).getName() + "");
                }

            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();
        isresume = false;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if (isok) {
                    finish();
                }
                // get item
                ListViewItem_my item = (ListViewItem_my) parent.getItemAtPosition(position);
                String titleStr = item.getTitle();
                String descStr = item.getDesc();
                Drawable iconDrawable = item.getIcon();
                String place = item.getPlace();

                if (titleStr.equals("쿠폰을 발급받아봅시다!")) {
                    finish();
                }

                db.collection("myuser").document(userid + "").collection("Coupon")
                        .whereEqualTo("Cname", titleStr + "")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                MapPoint DEFAULT_MARKER_POINT;
                                int i = 0;
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                    } else {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            if (!document.getId().equals("")) {
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                                                Date date = new Date();
                                                String strDate = dateFormat.format(date);
                                                data.put("usedTime", strDate);
                                                data.put("userid", userid + "");
                                                data.put("Cname", document.get("Cname") + "");
                                                data.put("Ccon", document.get("Ccon") + "");
                                                data.put("Cplace", document.get("Cplace") + "");
                                            }
                                        }
                                        db.collection("UserCouponHistory")
                                                .add(data)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        /*위험, 추후 수정한다면 쿠폰을 발급시에 쿠폰네임이아닌 자동ID로 해서 고유값을 가지게해 지워야한다.. 네임,장소로검색 ID가져와서 삭제*/
                                                        db.collection("myuser").document(userid + "").collection("Coupon").document(data.get("Cname") + "")
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        finish();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });

                                    }
                                } else {
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sep);
        db = FirebaseFirestore.getInstance();

        arr = new ArrayList<clist>();
        arr.clear();

        // Adapter 생성
        adapter = new ListViewAdapter_my();
        userid = getIntent().getStringExtra("userid") + "";
        isok = getIntent().getBooleanExtra("isok", false);

        if (isok) {
            db.collection("UserCouponHistory")
                    .whereEqualTo("userid", userid + "")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "쿠폰이 없습니다.", Toast.LENGTH_LONG).show();
                                //finish();
                            } else {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String name = document.getData().get("Cname") + "";
                                        String con = document.getData().get("Ccon") + "";
                                        String place = document.getData().get("Cplace") + "";
                                        String time = document.getData().get("usedTime") + "";
                                        arr.add(new clist(name, con, time, place));
                                    }
                                    makelist();
                                } else {
                                    Toast.makeText(getApplicationContext(), "쿠폰로딩실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        } else {

            db.collection("myuser").document(userid + "").collection("Coupon")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "쿠폰이 없습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String name = document.getData().get("Cname") + "";
                                        String con = document.getData().get("Ccon") + "";
                                        String place = document.getData().get("Cplace") + "";
                                        String time = document.getData().get("Ctime") + "";
                                        arr.add(new clist(name, con, time, place));
                                    }
                                    makelist();
                                } else {
                                    Toast.makeText(getApplicationContext(), "쿠폰로딩실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

        }


        isresume = false;

    }


    public void byungwooCode() {

        String data = getIntent().getStringExtra("analData");
        dataArray = data.split(",");
        for (int i = 0; i < dataArray.length; i++) {
            Log.v("자른 데이터", dataArray[i] + "");
        }

        ////////////////////보유중인 쿠폰 리스트 시작
        ListView listview;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bonobono1),
                "김밥 한줄 300원!", "2018년 1월 8일 21시 30분까지");
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bonobono2),
                "콜라 무료!!!!!!!!", "2018년 1월 8일 21시 34분까지");
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bonobono3),
                "가게 가져라!!!!", "2099년 12월 4일 11시 30분까지");


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String descStr = item.getDesc();
                Drawable iconDrawable = item.getIcon();

                // TODO : use item data.
            }
        });
        ///////////////////보유중인 쿠폰 리스트 끝


        ///////////////////추천 쿠폰 리스트뷰 시작
        ListView listview2;
        ListViewAdapter2 adapter2;

        // Adapter 생성
        adapter2 = new ListViewAdapter2();

        // 리스트뷰 참조 및 Adapter달기
        listview2 = (ListView) findViewById(R.id.listview2);
        listview2.setAdapter(adapter2);

        //1. 파이썬 분석결과 가져오기(추천1,추천2,추천3)
        /*추천 쿠폰 : 스타벅스, 엔제리너스, 베스킨이라고 가정
        넘어온 값 : "starbucks, angelinus, baskin"
        */

        //2. 분석결과에 해당하는 쿠폰정보 가져오기(FireBase)

        String a = "starbucks";
        String b = "angelinus";
        String c = "artbox";

        int img1 = 0;
        int img2 = 0;
        int img3 = 0;

        int[] arr = {R.drawable.adidas, R.drawable.angelinus, R.drawable.apple, R.drawable.artbox, R.drawable.baskin, R.drawable.burgerking, R.drawable.china1, R.drawable.china2, R.drawable.dessert1, R.drawable.dior, R.drawable.domino, R.drawable.hair1, R.drawable.hair2, R.drawable.hyundai, R.drawable.innisfree, R.drawable.japan1, R.drawable.japan2, R.drawable.kia, R.drawable.korean1, R.drawable.korean2, R.drawable.korean3, R.drawable.kyobo, R.drawable.nail1, R.drawable.nail2, R.drawable.officedepot, R.drawable.pizzahut, R.drawable.chanel, R.drawable.sony, R.drawable.starbucks, R.drawable.sulbing, R.drawable.swarovski, R.drawable.thebodyshop, R.drawable.western1, R.drawable.western2, R.drawable.ypbook, R.drawable.yvessaintlaurent,};
        String[] arrString = {"adidas", "angelinus", "apple", "artbox", "baskin", "burgerking", "china1", "china2", "dessert1", "dior", "domino", "hair1", "hair2", "hyundai", "innisfree", "japan1", "japan2", "kfc", "kia", "korean1", "korean2", "korean3", "kyobo", "nail1", "nail2", "officedepot", "pizzahut", "chanel", "sony", "starbucks", "sulbing", "swarovski", "thebodyshop", "western1", "western2", "ypbook", "yvessaintlaurent"};

        for (int i = 0; i < arr.length; i++) {
            if (arrString[i].equals(a)) {
                img1 = arr[i];
            } else if (arrString[i].equals(b)) {
                img2 = arr[i];
            } else if (arrString[i].equals(c)) {
                img3 = arr[i];
            }

        }


        //3.가져온 쿠폰정보 listView2에 추가
        // 첫 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(this, img1),
                "김밥 한줄 300원!", "2018년 1월 8일 21시 30분까지");
        // 두 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(this, img2),
                "콜라 무료!!!!!!!!", "2018년 1월 8일 21시 34분까지");
        // 세 번째 아이템 추가.
        adapter2.addItem(ContextCompat.getDrawable(this, img3),
                "가게 가져라!!!!", "2099년 12월 4일 11시 30분까지");


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr = item.getTitle();
                String descStr = item.getDesc();
                Drawable iconDrawable = item.getIcon();

                // TODO : use item data.
            }
        });
        ///////////////////추천 쿠폰 리스트뷰 끝
    }
}

