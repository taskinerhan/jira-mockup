package com.jwt.Dto;


import com.jwt.Enum.IssueStatus;
import com.jwt.Enum.IssueType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {
    private String title;
    private String description;
    private IssueType issueType;
    private IssueStatus status;
    private Long assignedUserId;
    private Long projectId;
}
