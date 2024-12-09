package com.jwt.service;


import com.jwt.Dto.IssueDTO;
import com.jwt.Entity.Issue;
import com.jwt.Entity.Project;
import com.jwt.Entity.User;
import com.jwt.Enum.IssueStatus;
import com.jwt.Enum.IssueType;
import com.jwt.Repository.IssueRepository;
import com.jwt.Repository.ProjectRepository;
import com.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;
//    public List<Issue> getUserIssues(String username) {
//        return issueRepository.findIssuesByUsername(username); // Parametreyi username olarak değiştirdik
//    }

    public Issue createIssue(IssueDTO issueDto) {
        Project project = projectRepository.findById(issueDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proje bulunamadı"));
        Optional<Issue> existingProject = issueRepository.findById(issueDto.getProjectId());
        if (existingProject.isPresent()) {
            throw new RuntimeException("Bu isimde zaten bir proje mevcut");
        }
        Issue issue = new Issue();
        issue.setTitle(issueDto.getTitle());
        issue.setDescription(issueDto.getDescription());
        issue.setIssueType(IssueType.valueOf(String.valueOf(issueDto.getIssueType())));
        issue.setStatus(IssueStatus.valueOf(String.valueOf(issueDto.getStatus())));
        issue.setProject(project);

        if (issueDto.getAssignedUserId() != null) {
            User user = userRepository.findById(issueDto.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
            issue.getAssignedUsers().add(user);
        }

        return issueRepository.save(issue);
    }
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElseThrow(() -> new RuntimeException("Issue not found"));
    }

    public Issue updateIssue(Long issueId, IssueDTO dto) {
        Issue issue = getIssueById(issueId);
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setIssueType(dto.getIssueType());
        issue.setStatus(dto.getStatus());
        return issueRepository.save(issue);
    }

    public void deleteIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issue.setDeleted(true);
        issueRepository.save(issue);
    }

    public Issue updateIssueStatus(Long issueId, String status) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Görev bulunamadı"));
        issue.setStatus(IssueStatus.valueOf(status));
        return issueRepository.save(issue);
    }
}
