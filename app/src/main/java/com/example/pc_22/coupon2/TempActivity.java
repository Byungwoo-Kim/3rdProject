package com.example.pc_22.coupon2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TempActivity extends Activity {
    FirebaseFirestore db;
    String targetName=null;
    ListView listview ;
    ListViewAdapter_my adapter;
    ArrayList<clist> arr;
Boolean isok = false;
String userid;
Boolean insert = false;
    String name;
    String con;
    String place;
    String time;
    int mytimer=0;
    Boolean res = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        db = FirebaseFirestore.getInstance();


        arr= new ArrayList<clist>();

        arr.clear();

        // Adapter 생성
        adapter = new ListViewAdapter_my() ;


        targetName = getIntent().getStringExtra("targetName")+"";
        userid = getIntent().getStringExtra("userid")+"";
        isok = getIntent().getBooleanExtra("isok",false);

        if(isok){
            db.collection("CouponPlace").document(targetName+"").collection("Coupon")
                    .whereEqualTo("Cis",true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "쿠폰이 없습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if (task.isSuccessful()) {
                                    for (final DocumentSnapshot document : task.getResult()) {
                                        name = document.getData().get("Cname")+"";
                                        con = document.getData().get("Ccon")+"";
                                        place = document.getData().get("Cplace")+"";
                                        time = document.getData().get("Ctime")+"";
                                        String str = String.valueOf(document.getData().get("Csec"));
                                        mytimer = Integer.parseInt(str);
                                        arr.add(new clist(name,con,time,place,mytimer));
                                    }
                                    for(int i = 0; i < arr.size(); i++) {
                                        final int ff = arr.size()-1;
                                        final int finalI = i;
                                        db.collection("myuser").document(userid + "").collection("Coupon")
                                                .whereEqualTo("Cname", arr.get(i).getName()+"")
                                                .whereEqualTo("Cplace", arr.get(i).getPlace()+"")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        Log.d("eeeee", task.getResult().size() + " ");
                                                        Log.d("eeeee", arr.size()+" arr sizz");
                                                        for(DocumentSnapshot doc : task.getResult()){
                                                            String str = (String)doc.getData().get("Cname")+"";
                                                            for (int i = 0; i < arr.size(); i++){
                                                                if(arr.get(i).getName().equals(str)){
                                                                    arr.remove(i);
                                                                }
                                                            }
                                                            Log.d("eeeee", doc.getData().get("Cname")+" zz");
                                                        }
                                                        Log.d("eeeee", arr.size()+" arr sizzz  " + finalI);
                                                        if(finalI >= ff){
                                                            res=true;
                                                            onResume();
                                                        }
                                                    }
                                                });
                                    }


                                } else {
                                    Toast.makeText(getApplicationContext(), "쿠폰로딩실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });


        }else{
            //title : 쿠폰 이름
            //desc : 유효기간 등 기타 정보
            //bonobono1 위치에 이미지 파일 이름
            db.collection("CouponPlace").document(targetName+"").collection("Coupon")
                    .whereEqualTo("Cis",false)
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
                                        String name = document.getData().get("Cname")+"";
                                        String con = document.getData().get("Ccon")+"";
                                        String place = document.getData().get("Cplace")+"";
                                        String time = document.getData().get("Ctime")+"";
                                        arr.add(new clist(name,con,time,place));
                                    }
                                    makelist();
                                } else {
                                    Toast.makeText(getApplicationContext(), "쿠폰로딩실패", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(res) {
            makelist2();
            res = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem_my item = (ListViewItem_my) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;
                String place = item.getPlace();
                mytimer = item.getMytimer();

                Intent intent = getIntent();
                String reusltText = titleStr;
                if(isok){
                    intent.putExtra("CouponName",reusltText);
                    intent.putExtra("CouponPlace",place);
                    intent.putExtra("mytimer",mytimer);
                    intent.putExtra("isok",isok);
                }else{
                    intent.putExtra("CouponName",reusltText);
                    intent.putExtra("CouponPlace",place);
                    intent.putExtra("isok",false);
                }


                setResult(RESULT_OK,intent);
                finish();
                // TODO : use item data.
            }
        }) ;

    }
    private void makelist2() {
        for (int i = 0; i < arr.size(); i++){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                    arr.get(i).getName()+"", arr.get(i).getCon()+"",arr.get(i).getTime()+"까지",arr.get(i).getPlace()+"",arr.get(i).getMytimer()+0) ;
            Log.d("ririri", arr.get(i).getName()+"");
        }
        adapter.notifyDataSetChanged();
    }
    private void makelist() {
        for (int i = 0; i < arr.size(); i++){
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_background),
                    arr.get(i).getName()+"", arr.get(i).getCon()+"",arr.get(i).getTime()+"까지",arr.get(i).getPlace()+"") ;
            Log.d("ririri", arr.get(i).getName()+"");
        }
        adapter.notifyDataSetChanged();
    }
}