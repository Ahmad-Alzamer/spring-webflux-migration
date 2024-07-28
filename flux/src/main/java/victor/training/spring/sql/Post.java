package victor.training.spring.sql;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@Table
public class Post {
  @Id
  private Long id;
  private String title;
  private String body;
  private Long authorId;

  @CreatedDate
  private LocalDateTime createdAt ;
  @CreatedBy
  private String createdBy;
}

