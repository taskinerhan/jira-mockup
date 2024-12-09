package com.jwt.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.Dto.IssueDTO;
import com.jwt.Dto.ProjectDTO;
import com.jwt.Entity.Issue;
import com.jwt.Entity.Project;
import com.jwt.Entity.User;
import com.jwt.Enum.IssueStatus;
import com.jwt.JsonSchemaValidator;
import com.jwt.Repository.ProjectRepository;
import com.jwt.Repository.UserRepository;
import com.jwt.service.IssueService;
import com.jwt.service.ProjectService;
import jakarta.validation.Valid;
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
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    private final IssueService issueService;
    private final JsonSchemaValidator projectSchemaValidator;
    private final JsonSchemaValidator issueSchemaValidator;
    private final JsonSchemaValidator statusSchemaValidator;
    private final UserRepository userRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    // Proje oluşturma
    @PostMapping()
    public ResponseEntity<?> createProject(@RequestBody String requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> user=userRepository.findByUsername(username);
            projectSchemaValidator.validate(requestBody);
            ProjectDTO projectDTO = objectMapper.readValue(requestBody, ProjectDTO.class);
            Project createdProject = projectService.createProject(user.get().getId(),projectDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Proje güncelleme
    @PutMapping("/update/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody String requestBody) {
        try {
            projectSchemaValidator.validate(requestBody);

            ProjectDTO projectDTO = objectMapper.readValue(requestBody, ProjectDTO.class);
            Project updatedProject = projectService.updateProject(projectId, projectDTO.getName(), projectDTO.getDescription());

            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Kullanıcıyı projeye ekleme
    @PostMapping("/{projectId}/assignUser/{userId}")
    public ResponseEntity<?> addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        try {
            Project updatedProject = projectService.addUserToProject(projectId, userId);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Görev ekleme
    @PostMapping("/issues")
    public ResponseEntity<?> addIssueToProject(@RequestBody String requestBody) {
        try {
            issueSchemaValidator.validate(requestBody);

            IssueDTO issueDTO = objectMapper.readValue(requestBody, IssueDTO.class);
            Issue newIssue = projectService.addIssueToProject(issueDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(newIssue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Kullanıcı projelerini listeleme  Admin
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserProjects(@PathVariable Long userId) {
        try {
            List<Project> projects = projectService.getProjectsByUser(userId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    // Kullanıcı projeleri listeleme
    @GetMapping("/user")
    public ResponseEntity<?> getUserProject() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> user=userRepository.findByUsername(username);
            Optional<Project> projects = projectRepository.findById(user.get().getId());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    // Proje silme
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
