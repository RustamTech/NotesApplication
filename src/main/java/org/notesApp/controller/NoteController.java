package org.notesApp.controller;

import lombok.RequiredArgsConstructor;
import org.notesApp.dto.NoteDto;
import org.notesApp.dto.NoteSummaryDto;
import org.notesApp.entity.Tag;
import org.notesApp.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Validated
public class NoteController {

  private final NoteService noteService;

  @PostMapping
  public ResponseEntity<?> createNote(@RequestBody NoteDto dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank()
            || dto.getText() == null || dto.getText().isBlank()) {
      return ResponseEntity.badRequest().body("Title and text must not be empty!");
    }

    try {
      NoteDto created = noteService.create(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateNote(@PathVariable String id, @RequestBody NoteDto dto) {
    try {
      NoteDto updated = noteService.update(id, dto);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteNote(@PathVariable String id) {
    noteService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getNoteById(@PathVariable String id) {
    NoteDto dto = noteService.getNoteById(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/summaries")
  public ResponseEntity<List<NoteSummaryDto>> getNoteSummaries(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "createdTime") String sortBy,
          @RequestParam(defaultValue = "desc") String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    List<NoteSummaryDto> summaries = noteService.getNoteSummaries(pageable);
    return ResponseEntity.ok(summaries);
  }

  @GetMapping("/filter")
  public ResponseEntity<?> getNotesByTags(
          @RequestParam(required = false) Set<String> tags,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdTime").descending());

    Set<Tag> tagEnums = new HashSet<>();
    if (tags != null && !tags.isEmpty()) {
      try {
        tagEnums = tags.stream()
                .map(String::toUpperCase)
                .map(Tag::valueOf)
                .collect(Collectors.toSet());
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body("Allowed tags: BUSINESS, PERSONAL, IMPORTANT");
      }
    }

    Page<NoteDto> notesPage = noteService.getNotesByTags(tagEnums, pageable);
    return ResponseEntity.ok(notesPage);
  }

  @GetMapping
  public ResponseEntity<Page<NoteDto>> getAllNotes(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size,
          @RequestParam(defaultValue = "createdTime") String sortBy,
          @RequestParam(defaultValue = "desc") String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<NoteDto> notes = noteService.getNotesByTags(Collections.emptySet(), pageable);
    return ResponseEntity.ok(notes);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    if (e.getMessage() != null && e.getMessage().startsWith("Note not found")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
  }
}
