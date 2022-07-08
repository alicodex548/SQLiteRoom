package com.example.sqliteroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskDialog.AddNewTaskCallback, TaskAdapter.TaskItemEventListener, EditTaskDialog.EditTaskCallback {
    private SQLiteHelper sqLiteHelper;
    private TaskAdapter taskAdapter = new TaskAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchEt = findViewById(R.id.et_main);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length()>0){
                    List<Task> tasks = sqLiteHelper.searchInTasks(s.toString());
                    taskAdapter.setTasks(tasks);
                }
                else {
                    List<Task> tasks = sqLiteHelper.getTasks();
                    taskAdapter.setTasks(tasks);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        RecyclerView recyclerView = findViewById(R.id.rv_main_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(taskAdapter);

        sqLiteHelper = new SQLiteHelper(this);
        List<Task> tasks = sqLiteHelper.getTasks();

        taskAdapter.addItems(tasks);

        View clearTasksBtn = findViewById(R.id.iv_main_clearTasks);
        clearTasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteHelper.clearAllTasks();
                taskAdapter.clearItems();
            }
        });

        View addNewTaskFab = findViewById(R.id.fab_main_addNewTask);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskDialog dialog = new AddTaskDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public void onNewTask(Task task) {
        long taskId = sqLiteHelper.addTask(task);
        if (taskId != -1){
            task.setId(taskId);
            taskAdapter.addItem(task);
        }
        else{
            Log.e("MainActivity", "Item Did Not Inserted: " );
        }
    }

    @Override
    public void onDeleteButtonClick(Task task) {
        int result = sqLiteHelper.deleteTask(task);
        if (result > 0){
            taskAdapter.deleteItem(task);
        }
    }

    @Override
    public void onItemLongPress(Task task) {
        EditTaskDialog editTaskDialog = new EditTaskDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task",task);
        editTaskDialog.setArguments(bundle);
        editTaskDialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onItemCheckedChange(Task task) {
        sqLiteHelper.updateTask(task);
    }

    @Override
    public void onEditTask(Task task) {
        int result = sqLiteHelper.updateTask(task);
        if (result>0){
            taskAdapter.updateItem(task);
        }
    }
}
