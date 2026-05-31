package com.example.nct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DangNhapActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        edtUsername = findViewById(R.id.editTextText);
        edtPassword = findViewById(R.id.editTextTextPassword);
        Button btnLogin = findViewById(R.id.button);
        imgBack = findViewById(R.id.img_back2);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStr = edtUsername.getText().toString().trim();
                String passStr = edtPassword.getText().toString().trim();

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                usersRef.orderByChild("username").equalTo(userStr).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null && user.getPassword().equals(passStr)) {
                                    Toast.makeText(DangNhapActivity.this, "Chào mừng " + user.getUsername(), Toast.LENGTH_SHORT).show();

                                    if ("ADMIN".equals(user.getRole())) {
                                        startActivity(new Intent(DangNhapActivity.this, AdminActivity.class));
                                    } else {
                                        startActivity(new Intent(DangNhapActivity.this, MainActivity.class));
                                    }
                                    finish();
                                    MainActivity.currentUser = user;
                                    return;
                                }
                            }
                            Toast.makeText(DangNhapActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DangNhapActivity.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhapActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        });

        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
                startActivity(intent);
            }
        });
    }
}
