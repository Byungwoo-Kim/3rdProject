package com.example.pc_22.coupon2;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class giftcouponActivity extends Activity {
    TextView tv1;
    TextView tv2;
    TextView tv3;
    FirebaseFirestore db;
    Map<String, Object> icoupon;
    TextView btn1;
    TextView btn2;
    String targetCouponName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_giftcoupon);
        tv1 = (TextView) findViewById(R.id.tv_c1);
        tv2 = (TextView) findViewById(R.id.tv_c2);
        tv3 = (TextView) findViewById(R.id.tv_c3);

        btn1 = (TextView) findViewById(R.id.btn_yes);
        btn2 = (TextView) findViewById(R.id.btn_no);
        db = FirebaseFirestore.getInstance();

        icoupon =new HashMap<>();
        icoupon = (Map<String, Object>) getIntent().getSerializableExtra("coupon");
        tv1.setText(icoupon.get("Cname")+"");
        tv2.setText(icoupon.get("Cplace")+"");
        tv3.setText("발급처 : " + icoupon.get("Cplace")+" " + "유효기간 : " + icoupon.get("Ctime") + "남은수량 : " + icoupon.get("Cquantity"));
        targetCouponName = icoupon.get("targetCouponName")+"";

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("myuser").document(icoupon.get("userid")+"").collection("Coupon").document(icoupon.get("Cname")+"").set(icoupon) .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                   /*쿠폰의 수량처리*/
                        Map<String, Object> coupon = new HashMap<>();
                        coupon.put("Cquantity", (int)icoupon.get("Cquantity")-1);
                        db.collection("CouponPlace").document(icoupon.get("targetName") + "").collection("Coupon").document(icoupon.get("Cname")+"").update(coupon).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                HashMap<String,Object> hm = new HashMap<>();
                                hm.put("Cplace",icoupon.get("Cplace"));
                                hm.put("Cname",icoupon.get("Cname"));
                                hm.put("userid",icoupon.get("userid"));
                                db.collection("CurrentUserPosition").document(icoupon.get("userid")+"").collection("Coupon").document("target").set(hm);

                                Toast.makeText(getApplicationContext(), "쿠폰이 정상적으로 발급되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        /*쿠폰수량마이너스실패*/
                                        Toast.makeText(getApplicationContext(), "쿠폰발급실패 : err2311", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                /*유저데이터에삽입실패*/
                                Toast.makeText(getApplicationContext(), "쿠폰발급실패 : err3313", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
