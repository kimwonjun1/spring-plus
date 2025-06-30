package org.example.expert.domain.todo.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoSearchCondition {

    private String titleKeyword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String nickname;

}
