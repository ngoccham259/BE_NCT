package com.example.nct;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DangKyActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword, edtCheck;
    private Button btnRegister;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtCheck = findViewById(R.id.edt_check);
        btnRegister = findViewById(R.id.button2);


        btnRegister.setOnClickListener(v -> {
            String username = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String checkPass = edtCheck.getText().toString().trim();

            // 1. Kiểm tra đầu vào
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(checkPass)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Tạo ID (Lấy timestamp làm ID để đảm bảo là số và không trùng lặp)
            int id = (int) (System.currentTimeMillis() / 1000);
            String role = "USER"; // Mặc định là khách hàng

            // 3. Sử dụng dòng code bạn yêu cầu
            User newUser = new User(id, username, password, role, email);

            // 4. Đẩy lên Firebase
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

            // Lưu vào node: users -> {id} -> {thong_tin_user}
            mDatabase.child(String.valueOf(id)).setValue(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DangKyActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình đăng nhập
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DangKyActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}