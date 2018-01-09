package com.example.pc_22.coupon2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {


    EditText et_id = null;
    EditText et_pw = null;
    TextView btn_join = null;
    ImageButton btn_login = null;
    FirebaseFirestore db;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        et_id = (EditText) findViewById(R.id.edt_id);
        et_pw = (EditText) findViewById(R.id.edt_pw);
        btn_join = (TextView) findViewById(R.id.txt_join);
        btn_login = (ImageButton) findViewById(R.id.imgbtn_login);

/**//**/
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, memberjoinActivity.class);
                startActivity(it);
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userId = et_id.getText().toString();
                String userPw = et_pw.getText().toString();

                String a = "";
                //파이썬에서 값 받아오는 코드 / 다른페이지에도 넣겠음
                boolean isNull = true;
                String sendmsg = "login";
                new Task(sendmsg).execute("sibal");
                Task task = new Task();
                while (isNull) {
                    a = task.receiveMsg;
                    if(a!=null){
                        isNull = false;
                    }
                }
                intent=new Intent(LoginActivity.this,MapActivity.class);
                intent.putExtra("analData", a + "");


                db.collection("myuser")
                        .whereEqualTo("id",userId+"")
                        .whereEqualTo("pw",userPw+"")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "아이디나 비밀번호가 틀림", Toast.LENGTH_SHORT).show();
                                }else{
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "로그인성공~", Toast.LENGTH_SHORT).show();
                                        for (DocumentSnapshot document : task.getResult()) {
                                           /*data*/
                                        }
                                        intent.putExtra("userid",userId+"");
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
    }
}