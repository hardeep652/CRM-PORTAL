package com.example.CRM.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CRM.model.Lead;
import com.example.CRM.model.Users;
import com.example.CRM.repository.LeadRepo;

@Service
public class LeadService {

    @Autowired
    private LeadRepo obj;

    public void saveLead(Lead l) {
        LocalDateTime now = LocalDateTime.now();
        l.setCreatedAt(now);
        l.setUpdatedAt(now); // initially same as createdAt
        obj.save(l);
    }

    public Lead getleadById(Long id) {
        return obj.findById(id).orElse(null);
    }

    public List<Lead> getLeadsByAssignedTo(Users u)
    {
        return obj.findByAssignedTo(u);
    }
    public void deleteLead(Lead l) {
        obj.delete(l);
    }

}
