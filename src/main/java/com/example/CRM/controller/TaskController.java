package com.example.CRM.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CRM.model.Lead;
import com.example.CRM.model.Task;
import com.example.CRM.model.Task.TaskStatus;
import com.example.CRM.model.Users;
import com.example.CRM.repository.UserRepo;
import com.example.CRM.service.LeadService;
import com.example.CRM.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService obj;

    @Autowired
    private UserRepo userRepo; // Assuming you have a UserRepo to fetch user details

    @Autowired
    private LeadService leadService; // Assuming you have a LeadService to fetch leads

    @PostMapping("/newTask")
    public String createTask(@RequestBody Task t) {
        Users dummy = userRepo.findById(1L).orElse(null); // Replace with actual logged-in user logic
        if (dummy == null) {
            return "User not found";
        }
        t.setAssignedTo(dummy);

        List<Lead> Leads = leadService.getLeadsByAssignedTo(dummy);
        if (Leads.isEmpty()) {
            return "No leads found for the assigned user";
        }
        for (Lead Lead : Leads) {
            if (t.getRelatedLead().getId().equals(Lead.getId())) {
                t.setRelatedLead(Lead);
                break; // Exit loop once the related lead is found
            }
        }
        if (t.getRelatedLead() == null) {
            return "Related lead not found for the task";
        }

        obj.saveTask(t);
        return "The task was created: " + t;
    }

    @GetMapping("/myTasks")
    public List<Map<String, Object>> getMyTasks() {
        Users dummy = userRepo.findById(1L).orElse(null); // Dummy logged-in user
        if (dummy == null) {
            return List.of();
        }

        List<Task> tasks = obj.getTasksByAssignedTo(dummy);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Task t : tasks) {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("id", t.getId());
            taskMap.put("title", t.getTitle());
            taskMap.put("description", t.getDescription());
            taskMap.put("status", t.getStatus());
            taskMap.put("dueDate", t.getDueDate());

            // assignedTo's name
            taskMap.put("assignedTo", t.getAssignedTo() != null ? t.getAssignedTo().getName() : null);

            // related lead's name and company
            if (t.getRelatedLead() != null) {
                taskMap.put("relatedLeadName", t.getRelatedLead().getName());
                taskMap.put("relatedCompany", t.getRelatedLead().getCompany());
            } else {
                taskMap.put("relatedLeadName", null);
                taskMap.put("relatedCompany", null);
            }

            response.add(taskMap);
        }

        return response;
    }

    @PutMapping("/updateTask")
    public String updateTask(@RequestBody Task t) {
        Users dummy = userRepo.findById(1L).orElse(null); // Dummy logged-in user
        if (dummy == null) {
            return "User not found";
        }

        List<Task> tasks = obj.getTasksByAssignedTo(dummy);

        for (Task task : tasks) {
            if (task.getId().equals(t.getId())) {
                // Check status
                if (t.getStatus() == TaskStatus.COMPLETED) {
                    obj.deleteTask(task); // delete task if completed
                    return "The task was completed and deleted successfully.";
                }

                // Otherwise update fields
                task.setTitle(t.getTitle());
                task.setDescription(t.getDescription());
                task.setStatus(t.getStatus());
                task.setDueDate(t.getDueDate());
                obj.saveTask(task);

                return "The task was updated successfully.";
            }
        }

        return "Task not found or not assigned to you.";
    }

}
