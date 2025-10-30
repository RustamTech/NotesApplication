package org.notesApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class NoteDto {
  private String id;
  private String title;
  private LocalDateTime createdTime;
  private String text;
  private Set<String> tags;
  private Map<String, Long> wordStats;

}
