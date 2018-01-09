package com.example.pc_22.coupon2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class memberjoinActivity extends AppCompatActivity {
    TextView joinButton = null;
    ImageButton imgbtn_back = null;
    EditText EtId = null;
    EditText EtPw = null;
    EditText EtAge = null;
    RadioGroup radioGroup = null;

    Spinner spinner;
    Spinner spinner1;
    Spinner spinner2;
    FirebaseFirestore db;

    String id;
    String pw;
    String age;
    String interest1;
    String interest2;
    String interest3;
    int genderId;
    Map<String, Object> user = new HashMap<>();
    RadioButton rb;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberjoin);

        db = FirebaseFirestore.getInstance();
        EtId = (EditText) findViewById(R.id.edt_id);
        EtPw = (EditText) findViewById(R.id.edt_pw);
        EtAge = (EditText) findViewById(R.id.edt_age);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

        joinButton = (TextView) findViewById(R.id.btn_join);
        imgbtn_back = (ImageButton) findViewById(R.id.imgbtn_back);
        spinner = (Spinner) findViewById(R.id.spn_interest1);
        spinner1 = (Spinner) findViewById(R.id.spn_interest2);
        spinner2 = (Spinner) findViewById(R.id.spn_interest3);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.number, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.number2, android.R.layout.simple_spinner_item);
        spinner1.setAdapter(adapter2);

        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this, R.array.number3, android.R.layout.simple_spinner_item);
        spinner2.setAdapter(adapter3);


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db에 저장 해야됨!
                id = EtId.getText().toString();
                pw = EtPw.getText().toString();
                age = EtAge.getText().toString();
                interest1 = spinner.getSelectedItem().toString();
                interest2 = spinner1.getSelectedItem().toString();
                interest3 = spinner2.getSelectedItem().toString();
                genderId = radioGroup.getCheckedRadioButtonId();

                rb = (RadioButton) findViewById(genderId);
                gender = rb.getText().toString();

                user.put("id", id+"");
                user.put("pw", pw+"");
                user.put("age", age+"");
                user.put("interest1", interest1+"");
                user.put("interest2", interest2+"");
                user.put("interest3", interest3+"");
                user.put("gender", gender+"");
                Log.v("값", id + "" + pw + age + interest1 + interest2 + interest3 + gender);

                db.collection("myuser")
                        .whereEqualTo("id", id+"")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    db.collection("myuser").document(user.get("id")+"")
                                            .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("Cname","");
                                            data.put("Cplace","");
                                            db.collection("myuser").document(user.get("id")+"").collection("Coupon").document().set(data);
                                            Toast.makeText(getApplicationContext(), "회원가입을 환영합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("삽입실패", "Error writing document", e);
                                                }
                                            });
                                } else {
                                    Toast.makeText(getApplicationContext(), "This ID is already taken. Please use another ID", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                //서버로 값 넘기기
                String sendmsg = "join";
                try {
                        id = URLEncoder.encode(id,"utf-8");
                        interest1 = URLEncoder.encode(interest1,"utf-8");
                        interest2 = URLEncoder.encode(interest2,"utf-8");
                        interest3 = URLEncoder.encode(interest3,"utf-8");
                        gender = URLEncoder.encode(gender,"utf-8");

                    //Task task = new Task();
                    //task.execute("?log=join&id="+id+"&age="+age+"&gender="+gender+"&interest1="+interest1+"&interest2="+interest2+"&interest3="+interest3);
                    new Task(sendmsg).execute("join",id,age,gender,interest1,interest2,interest3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent it = new Intent(memberjoinActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        });


        imgbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(memberjoinActivity.this, LoginActivity.class);
                startActivity(it);
                finish();


            }
        });

    }
}
