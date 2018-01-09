package com.example.pc_22.coupon2;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btn_login;
    Button btn_join;
    TextView tv_id;
    TextView tv_pw;
    EditText et_id;
    EditText et_pw;
    FirebaseFirestore db;


   /* boolean bLog = false; // false : 로그아웃 상태
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("test", "onCreateOptionsMenu - 최초 메뉴키를 눌렀을 때 호출됨");
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
                "화면에 보여질때 마다 호출됨");
        if(bLog){ // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }
        bLog = !bLog;   // 값을 반대로 바꿈
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("test", "onOptionsItemSelected - 메뉴항목을 클릭했을 때 호출됨");

        int id = item.getItemId();


        switch(id) {
            case R.id.menu_login:
                Toast.makeText(getApplicationContext(), "로그인 메뉴 클릭",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                Toast.makeText(getApplicationContext(), "로그아웃 메뉴 클릭",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_a:
                Toast.makeText(getApplicationContext(), "다음",
                        Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
   Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        btn_login = (Button) findViewById(R.id.btn1);
        btn_join = (Button) findViewById(R.id.btn2);
        tv_id = (TextView) findViewById(R.id.tv_c1);
        tv_pw = (TextView) findViewById(R.id.tv2);
        et_id = (EditText) findViewById(R.id.ed1);
        et_pw = (EditText) findViewById(R.id.ed2);



        /*
        Map<String, Object> user = new HashMap<>();
        user.put("Cname", "쿠폰이름");
        user.put("Ccon", "쿠폰내용");
        user.put("Ctime", "쿠폰유효기간");
        user.put("Cmany", 2);
        db.collection("CouponPlacePosition").document().collection("Coupon").add(user);

*/


        db.collection("CurrentUserPosition")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int i = 0;
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                               i++;
                            }
                            tv_id.setText("현재 접속중인 이용자수 : " + i+"");
                        } else {

                        }
                    }
                });

        db.collection("CurrentUserPosition")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {

                            return;
                        }else{
                            db.collection("CurrentUserPosition")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            int i = 0;
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    i++;
                                                }
                                                tv_id.setText("현재 접속중인 이용자수 : " + i+"");
                                            } else {

                                            }
                                        }
                                    });

                        }




                    }
                });

        /*key*/
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db.collection("myuser")
                        .whereEqualTo("id", et_id.getText() + "")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("id", et_id.getText() + "");
                                    user.put("pw", et_pw.getText() + "");
                                    db.collection("myuser").document(et_id.getText() + "")
                                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("Cname","");
                                            data.put("Cplace","");
                                            db.collection("myuser").document(et_id.getText()+"").collection("Coupon").document().set(data);
                                            Toast.makeText(getApplicationContext(), "회원가입을 환영합니다.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("삽입실패", "Error writing document", e);
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "This ID is already taken. Please use another ID", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("myuser")
                        .whereEqualTo("id",et_id.getText()+"")
                        .whereEqualTo("pw",et_pw.getText()+"")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "아이디나 비밀번호가 틀림", Toast.LENGTH_LONG).show();
                                }else{
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "로그인성공~", Toast.LENGTH_LONG).show();
                                        for (DocumentSnapshot document : task.getResult()) {
                                            tv_id.setText(document.getId());
                                            tv_pw.setText(document.getString("pw"));
                                            Log.d("ddd", document.getId() + " => " + document.getData());
                                        }
                                        Intent intent=new Intent(MainActivity.this,MapActivity.class);
                                        intent.putExtra("userid",et_id.getText()+"");
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("ddd", "Error getting documents: ", task.getException());
                                    }
                                }

                            }
                        });

            }


        });

/*
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Alan");
        user.put("middle", "Mathison");
        user.put("last", "Turring");
        user.put("born", 1912);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("데이터삽입성공", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("데이터삽입실패", "Error adding document", e);
                    }
                });

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("데이터불러옴", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("데이터불러오기실패", "Error getting documents.", task.getException());
                        }
                    }
                });


        Map<String, Object> city = new HashMap<>();
        city.put("name", "Los Angeles");
        city.put("state", "CA");
        city.put("country", "미쿡");



        db.collection("cities").document("LA")
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("삽입성공", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("삽입실패", "Error writing document", e);
                    }
                });

        Map<String, Object> data = new HashMap<>();
        data.put("capital", true);

        db.collection("cities").document("LA")
                .set(data, SetOptions.merge());

        db.collection("cities").document("LA")
                .update(
                        "age", 13,
                        "favorites.color", "Red",
                        "또라이","죽어볼래?"
                );
*//*문서에 중첩된 개체가 있는 경우 update()를 호출할 때 '점 표기법'을 사용하여 문서 내의 중첩된 필드를 참조할 수 있습니다.*//*
        *//*특정칼럼의 데이터 업데이트*//*
        db.collection("cities").document("LA") .update("capital", false);
        *//*  사전의 구조 *//*
        *//* 컬렉션(포장지) 컬렉션이없으면 자동으로 생성, 컬렉션의 하위 문서가 없으면 컬렉션도 자동삭제*//*
        *//* 컬렉션 -> 문서(doc)의 구조로 이루어짐 문서의 ID를 지정하려면 ADD가아닌 document("문서이름").SET으로 삽입*//*
        *//* 문서의 업데이트는 기본적으로 같은 id를 지정시 덮어씌움 *//*
        *//* 문서에 해당 KEY가없으면 자동적으로 추가됨*//*


        *//*업데이트 시점 타임스탬프로 남기기(sysdate)*//*
        Map<String,Object> updates = new HashMap<>();
        updates.put("timestamp", FieldValue.serverTimestamp());
        db.collection("cities").document("LA") .update(updates);*/
    }
}
