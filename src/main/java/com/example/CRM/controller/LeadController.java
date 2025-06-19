package com.example.CRM.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CRM.model.Client;
import com.example.CRM.model.Lead;
import com.example.CRM.model.Users;
import com.example.CRM.model.Lead.LeadStatus;
import com.example.CRM.repository.UserRepo;
import com.example.CRM.service.ClientService;
import com.example.CRM.service.LeadService;


@RestController
@RequestMapping("/api/Leads")
public class LeadController {

    @Autowired
    private LeadService obj;

    @Autowired
    private ClientService clientService; // Add this if you want to save clients

    @Autowired
    private UserRepo userRepo; // Add this

    @PostMapping("/newLead")
    public String generateLead(@RequestBody Lead lead) {

        // 🔧 Temporary dummy assignment for testing
        Users dummyUser = userRepo.findById(1L).orElse(null); // hardcoded ID
        lead.setAssignedTo(dummyUser);

        obj.saveLead(lead);

        return "The lead was created: " + lead;
    }

    @GetMapping("/myLeads")
    public List<Map<String, Object>> getdummyleads() {
        Users dummy = userRepo.findById(1L).orElse(null); // hardcoded ID

        if (dummy != null) {
            List<Lead> leads = obj.getLeadsByAssignedTo(dummy);

            List<Map<String, Object>> response = new ArrayList<>();

            for (Lead l : leads) {
                Map<String, Object> leadMap = new HashMap<>();
                leadMap.put("id", l.getId());
                leadMap.put("name", l.getName());
                leadMap.put("email", l.getEmail());
                leadMap.put("phone", l.getPhone());
                leadMap.put("company", l.getCompany());
                leadMap.put("status", l.getStatus());
                leadMap.put("createdAt", l.getCreatedAt());
                leadMap.put("updatedAt", l.getUpdatedAt());
                leadMap.put("assignedToName", l.getAssignedTo().getName()); // 👈 only name shown

                response.add(leadMap);
            }

            return response;
        } else {
            return new ArrayList<>();
        }
    
     }


     @PutMapping("/updateLead")
     public ResponseEntity<String> updateLead(@RequestBody Lead l) {

         // 1. Get the dummy logged-in user (replace with Spring Security later)
         Users dummyUser = userRepo.findById(1L).orElse(null);

         if (dummyUser == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
         }

         // 2. Get all leads assigned to this user
         List<Lead> employeeLeads = obj.getLeadsByAssignedTo(dummyUser);

         // 3. Check if this lead belongs to the user
         for (Lead lead : employeeLeads) {
             if (lead.getId().equals(l.getId())) {

                 // 4. Found the lead — update allowed fields
                 Lead existingLead = obj.getleadById(l.getId());
                 if (existingLead != null) {
                     existingLead.setName(l.getName());
                     existingLead.setEmail(l.getEmail());
                     existingLead.setPhone(l.getPhone());
                     existingLead.setCompany(l.getCompany());
                     existingLead.setStatus(l.getStatus());
                     existingLead.setUpdatedAt(LocalDateTime.now());

                     // ✅ If converting to client, create client and delete lead
                     if (l.getStatus() == LeadStatus.CONVERTED) {
                         Client client = new Client();
                         client.setName(existingLead.getName());
                         client.setEmail(existingLead.getEmail());
                         client.setPhone(existingLead.getPhone());
                         client.setCompany(existingLead.getCompany());
                         client.setAddress("N/A");
                         client.setCreatedAt(LocalDateTime.now());
                         client.setAssignedTo(existingLead.getAssignedTo());

                         clientService.saveClient(client);

                         obj.deleteLead(existingLead); // ✅ now it's safe to delete

                         return ResponseEntity.ok("Lead converted to client and deleted successfully.");
                     } else {
                         // 🔁 Only update lead if not being converted
                         obj.saveLead(existingLead);
                         return ResponseEntity.ok("Lead updated successfully: " + existingLead);
                     }
                 } else {
                     return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Lead not found in database");
                 }
             }
         }

         // 6. If loop completes without finding a match — access denied
         return ResponseEntity.status(HttpStatus.FORBIDDEN)
                 .body("You are not authorized to update this lead");
     }


    
}
