package com.example.CRM.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CRM.model.Task;
import com.example.CRM.model.Users;
import com.example.CRM.repository.TaskRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo obj;

    public void saveTask(Task t)
    {
        LocalDateTime now = LocalDateTime.now();
        t.setCreatedAt(now);
        t.setUpdatedAt(now);
        // Save the task to the repository
        obj.save(t);
        
    }
    public List<Task> getTasksByAssignedTo(Users u)
    {
        return obj.findByAssignedTo(u);
    }
    
    public void deleteTask(Task t) {
    obj.delete(t);  // obj is your TaskRepo
}

}
