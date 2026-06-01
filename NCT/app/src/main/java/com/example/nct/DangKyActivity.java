package com.example.nct;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DangKyActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword, edtCheck, edtPhone;
    private Button btnRegister;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone); 
        edtPassword = findViewById(R.id.edt_password);
        edtCheck = findViewById(R.id.edt_check);
        btnRegister = findViewById(R.id.button2);
        imgBack = findViewById(R.id.img_back);

        // Sự kiện click cho icon Back
        imgBack.setOnClickListener(v -> {
            finish(); // Quay lại trang đăng nhập (đóng Activity hiện tại)
        });

        btnRegister.setOnClickListener(v -> {
            String username = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = (edtPhone != null) ? edtPhone.getText().toString().trim() : "";
            String password = edtPassword.getText().toString().trim();
            String checkPass = edtCheck.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(checkPass)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = (int) (System.currentTimeMillis() / 1000);
            String role = "user";

            // Truyền 5 tham số khớp với User.java hiện tại
            User newUser = new User(id, username, password, role, email);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.child(String.valueOf(id)).setValue(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DangKyActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
