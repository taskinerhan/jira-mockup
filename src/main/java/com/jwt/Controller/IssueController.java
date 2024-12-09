package com.jwt.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.Dto.IssueDTO;
import com.jwt.Entity.Issue;
import com.jwt.Entity.User;
import com.jwt.Enum.IssueStatus;
import com.jwt.JsonSchemaValidator;
import com.jwt.Repository.IssueRepository;
import com.jwt.Repository.UserRepository;
import com.jwt.service.EmailService;
import com.jwt.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final JsonSchemaValidator issueSchemaValidator;
    private final JsonSchemaValidator updateIssueSchemaValidator;

    private final EmailService emailService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Proje için yeni görev
    @PostMapping("/issues/{projectId}")
    public ResponseEntity<?> createIssue(@PathVariable Long projectId, @RequestBody String requestBody) {
        try{
            issueSchemaValidator.validate(requestBody);
            IssueDTO issueDto=objectMapper.readValue(requestBody,IssueDTO.class);
            issueDto.setProjectId(projectId);
            Issue createdIssue = issueService.createIssue(issueDto);
            return ResponseEntity.ok(createdIssue);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    // Kullanıcının atanmış olduğu issue
    @GetMapping("/issues")
    public ResponseEntity<?> getUserIssues() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("NAME: " + username);

            List<Issue> issues= issueRepository.findIssuesByAssignedId(currentUser.getId());
            return ResponseEntity.ok(issues);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    //issue günceller
    @PutMapping("/{issueId}")
    public ResponseEntity<?> updateIssue(@PathVariable Long issueId ,@RequestBody String requestBody) {
        try {
            updateIssueSchemaValidator.validate(requestBody);
            IssueDTO issueDto=objectMapper.readValue(requestBody,IssueDTO.class);
            Issue updatedIssue = issueService.updateIssue(issueId, issueDto);
            return ResponseEntity.ok(updatedIssue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    @DeleteMapping("/{issueId}")
    public ResponseEntity<?> deleteIssue(@PathVariable Long issueId) {
        try {
            issueService.deleteIssue(issueId);
            return ResponseEntity.ok(Map.of("message", "Issue deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    // Görev durumunu güncelleme
    @PatchMapping("/issues/{issueId}/status")
    public ResponseEntity<?> updateIssueStatus(@PathVariable Long issueId, @RequestBody String requestBody) {
        try {
            Map jsonMap = objectMapper.readValue(requestBody, Map.class);
            System.out.println(jsonMap);

            String statusValue = (String) jsonMap.get("status");
            IssueStatus status = IssueStatus.valueOf(statusValue);
            Issue updatedIssue = issueService.updateIssueStatus(issueId, status.toString());
            List<Issue> issueList=issueRepository.findIssuesByAssignedId(issueId);

            for (Issue item : issueList) {
                emailService.sendEmail(String.valueOf(item), "Subject", "Content of the email");
            }

            return ResponseEntity.ok(updatedIssue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hata: " + e.getMessage());
        }
    }
}
