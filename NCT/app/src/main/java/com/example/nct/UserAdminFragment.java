package com.example.nct;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class UserAdminFragment extends Fragment implements AdminUserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private AdminUserAdapter adapter;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_admin, container, false);

        recyclerView = view.findViewById(R.id.rv_admin_users);
        tabLayout = view.findViewById(R.id.tab_layout_users);
        fabAdd = view.findViewById(R.id.fab_add_user);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminUserAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterUsers(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        fabAdd.setOnClickListener(v -> showAddEditUserDialog(null, -1));

        // Mặc định hiển thị tab Admin (vị trí 0)
        filterUsers(0);

        return view;
    }

    private void filterUsers(int position) {
        String targetRole = (position == 0) ? "admin" : "user";
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : UserManager.allUsers) {
            if (user.getRole().equalsIgnoreCase(targetRole)) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }

    private void showAddEditUserDialog(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_user, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_user_title);
        EditText etUsername = dialogView.findViewById(R.id.et_user_username);
        EditText etEmail = dialogView.findViewById(R.id.et_user_email);

        EditText etPassword = dialogView.findViewById(R.id.et_user_password);        
        Spinner spinnerRole = dialogView.findViewById(R.id.spinner_user_role);

        String[] roles = {"admin", "user"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        if (user != null) {
            tvTitle.setText("Sửa người dùng");
            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());

            etPassword.setText(user.getPassword());
            spinnerRole.setSelection(user.getRole().equalsIgnoreCase("admin") ? 0 : 1);
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_user_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_user_save).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() ) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) {
                // Thêm mới - Tạo ID ngẫu nhiên từ timestamp
                int id = (int) (System.currentTimeMillis() / 1000);
                User newUser = new User(id, username, password, role, email);
                UserManager.addUser(newUser); // Lưu lên Firebase
                Toast.makeText(getContext(), "Đã thêm người dùng", Toast.LENGTH_SHORT).show();
            } else {
                // Sửa thông tin
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setRole(role);
                UserManager.addUser(user); // Cập nhật lên Firebase
                Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
            }
            filterUsers(tabLayout.getSelectedTabPosition());
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onEditClick(User user, int position) {
        showAddEditUserDialog(user, position);
    }

    @Override
    public void onDeleteClick(User user, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa " + user.getUsername() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    UserManager.deleteUser(user); // Xóa trên cả Firebase
                    filterUsers(tabLayout.getSelectedTabPosition());
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
