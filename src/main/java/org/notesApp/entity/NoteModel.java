package org.notesApp.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notes")
public class NoteModel {
  @Id
  private String id;

  @NotNull
  @Indexed
  private LocalDateTime createdTime;

  @NotBlank(message = "Requirement: Title can not be empty")
  private String title;

  @NotBlank(message = "Requirement: Text can not be empty")
  private String text;

  private Set<Tag> tags;

}
