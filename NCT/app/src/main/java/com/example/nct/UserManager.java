package com.example.nct;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class UserManager {
    public static ArrayList<User> allUsers = new ArrayList<>();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

    static {
        // Tài khoản mẫu mặc định (Đã xóa phone)
        allUsers.add(new User(1, "admin", "admin", "admin", "admin@gmail.com"));
        allUsers.add(new User(2, "khachhang", "123", "user", "user@gmail.com"));

        // Lắng nghe dữ liệu từ Firebase để đồng bộ danh sách allUsers
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> tempUsers = new ArrayList<>();
                // Thêm lại các tài khoản hệ thống cố định
                tempUsers.add(new User(1, "admin", "admin", "admin", "admin@gmail.com"));
                
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user != null) {
                        // Tránh trùng lặp với tài khoản admin hệ thống
                        if (!user.getUsername().equals("admin")) {
                            tempUsers.add(user);
                        }
                    }
                }
                allUsers = tempUsers;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static User login(String username, String password) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        // Lưu lên Firebase
        mDatabase.child(String.valueOf(user.getId())).setValue(user);
    }
    
    public static void deleteUser(User user) {
        mDatabase.child(String.valueOf(user.getId())).removeValue();
    }
}
