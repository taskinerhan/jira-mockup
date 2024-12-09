package com.jwt.service;


import com.jwt.Dto.IssueDTO;
import com.jwt.Dto.ProjectDTO;
import com.jwt.Entity.Issue;
import com.jwt.Entity.Project;
import com.jwt.Entity.User;
import com.jwt.Repository.IssueRepository;
import com.jwt.Repository.ProjectRepository;
import com.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    public Project createProject(Long userId,ProjectDTO projectDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Project existingProject = projectRepository.findByName(projectDto.getName());
        if (existingProject != null) {
            throw new RuntimeException("There is already a project with this name");
        }
        Project project = new Project();
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setCreatedBy(user);
        project.getAssignedUsers().add(user);
        return projectRepository.save(project);
    }

    public Project updateProject(Long projectId, String name, String description) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));
        project.setName(name);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    public List<Project> getDeletedProjects() {
        return projectRepository.findDeletedProjects();
    }

    public Project addUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndCreatedBy_Id(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project or user not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        project.getAssignedUsers().add(user);
        return projectRepository.save(project);
    }


    public Issue addIssueToProject(IssueDTO issueDto) {
        Project project = projectRepository.findById(issueDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        User assignedUser = userRepository.findById(issueDto.getAssignedUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Issue issue = new Issue();
        issue.setTitle(issueDto.getTitle());
        issue.setDescription(issueDto.getDescription());
        issue.setIssueType(issueDto.getIssueType());
        issue.setStatus(issueDto.getStatus());
        issue.setProject(project);
        issue.getAssignedUsers().add(assignedUser);

        return issueRepository.save(issue);
    }


    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project is not found"));
        project.setDeleted(true);
        projectRepository.save(project);
    }


    // Kullanıcının projelerini listeleme  ?
    public List<Project> getProjectsByUser(Long userId) {
        return projectRepository.findByCreatedBy_Id(userId);
    }
}
