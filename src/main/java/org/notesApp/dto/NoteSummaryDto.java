package org.notesApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoteSummaryDto {
  private String id;
  private String title;
  private LocalDateTime createdTime;
}
