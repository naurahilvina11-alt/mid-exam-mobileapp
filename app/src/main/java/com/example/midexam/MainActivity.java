package com.example.midexam;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText etName, etStudentId;
    private CheckBox cbEnrolled;
    private ListView listView;
    private ArrayAdapter<Student> adapter;

    private final List<Student> students = new ArrayList<>();
    private Student selectedStudent = null;
    private boolean listVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etStudentId = findViewById(R.id.etStudentId);
        cbEnrolled = findViewById(R.id.cbEnrolled);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, students);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Student s = students.get(position);
            selectedStudent = s;
            etName.setText(s.getName());
            etStudentId.setText(s.getStudentId());
            cbEnrolled.setChecked(s.isEnrolled());
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnView = findViewById(R.id.btnView);

        btnAdd.setOnClickListener(v -> addStudent());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> deleteStudent());
        btnView.setOnClickListener(v -> {
            listVisible = true;
            clearForm();
            loadStudents();
            Toast.makeText(this, "Total data: " + students.size(), Toast.LENGTH_SHORT).show();
        });

    }

    private void addStudent() {
        String name = etName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        if (!validate(name, studentId)) return;

        if (db.addStudent(name, studentId, cbEnrolled.isChecked())) {
            Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            clearForm();
            loadStudents();
        } else {
            Toast.makeText(this, "Gagal menambah data. Student ID mungkin sudah ada.", Toast.LENGTH_LONG).show();
        }
    }

    private void editStudent() {
        if (selectedStudent == null) {
            Toast.makeText(this, "Pilih data di list", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        if (!validate(name, studentId)) return;

        if (db.updateStudent(selectedStudent.getId(), name, studentId, cbEnrolled.isChecked())) {
            Toast.makeText(this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
            clearForm();
            loadStudents();
        } else {
            Toast.makeText(this, "Gagal mengubah data. Student ID mungkin sudah dipakai.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteStudent() {
        final Student selected = selectedStudent;
        if (selected == null) {
            Toast.makeText(this, "Pilih data di list", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Hapus data")
                .setMessage("Hapus " + selected.getName() + " (" + selected.getStudentId() + ")?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    if (db.deleteStudent(selected.getId())) {
                        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                        clearForm();
                        loadStudents();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private boolean validate(String name, String studentId) {
        if (name.isEmpty()) {
            etName.setError("Nama tidak boleh kosong");
            return false;
        }
        if (studentId.isEmpty()) {
            etStudentId.setError("Student ID tidak boleh kosong");
            return false;
        }
        return true;
    }

    private void loadStudents() {
        students.clear();
        if (listVisible) {
            students.addAll(db.getAllStudents());
        }
        adapter.notifyDataSetChanged();
    }

    private void clearForm() {
        etName.setText("");
        etStudentId.setText("");
        cbEnrolled.setChecked(false);
        selectedStudent = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
    }
}