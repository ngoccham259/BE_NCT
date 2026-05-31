package com.example.nct;

import java.util.ArrayList;

public class UserManager {
    public static ArrayList<User> allUsers = new ArrayList<>();

    static {
        // Tài khoản mẫu
        allUsers.add(new User(1,"admin", "admin", "admin", "admin@gmail.com"));
        allUsers.add(new User(1,"khachhang", "123", "user", "user@gmail.com"));
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
        allUsers.add(user);
    }
}
