package com.example.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.room.AddTaskDialog;
import com.example.room.AppDatabase;
import com.example.room.EditTaskDialog;
import com.example.room.Task;
import com.example.room.TaskAdapter;
import com.example.room.TaskDao;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskDialog.AddNewTaskCallback, TaskAdapter.TaskItemEventListener, EditTaskDialog.EditTaskCallback {
    private TaskDao taskDao;
    private TaskAdapter taskAdapter = new TaskAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDao = AppDatabase.getAppDatabase(this).getTaskDao();

        EditText searchEt = findViewById(R.id.et_main);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length()>0){
                    List<Task> tasks = taskDao.search(s.toString());
                    taskAdapter.setTasks(tasks);
                }
                else {
                    List<Task> tasks = taskDao.getAll();
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

        List<Task> tasks = taskDao.getAll();

        taskAdapter.addItems(tasks);

        View clearTasksBtn = findViewById(R.id.iv_main_clearTasks);
        clearTasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskDao.deleteAll();
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
        long taskId = taskDao.add(task);
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
        int result = taskDao.delete(task);
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
        taskDao.update(task);
    }

    @Override
    public void onEditTask(Task task) {
        int result = taskDao.update(task);
        if (result>0){
            taskAdapter.updateItem(task);
        }
    }
}
