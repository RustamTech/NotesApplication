package org.testNotesApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.notesApp.dto.NoteDto;
import org.notesApp.entity.NoteModel;
import org.notesApp.entity.Tag;
import org.notesApp.repository.NoteRepository;
import org.notesApp.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoteUnitTests {

  @Mock
  private NoteRepository noteRepository;

  @InjectMocks
  private NoteService noteService;

  private NoteModel noteModel;

  @BeforeEach
  void setup() {
    noteModel = NoteModel.builder()
            .id("1")
            .title("Test Note")
            .text("Hello world hello again")
            .tags(Set.of(Tag.BUSINESS, Tag.PERSONAL))
            .createdTime(LocalDateTime.now())
            .build();
  }

  @Test
  void testCreateNote() {
    NoteDto dto = NoteDto.builder()
            .title("Test Note")
            .text("Hello world hello again")
            .tags(Set.of("BUSINESS", "PERSONAL"))
            .build();

    lenient().when(noteRepository.save(any(NoteModel.class))).thenReturn(noteModel);

    NoteDto created = noteService.create(dto);

    assertEquals("Test Note", created.getTitle());
    assertEquals(2L, created.getWordStats().get("hello"));
    assertTrue(created.getTags().contains("BUSINESS"));
    assertTrue(created.getTags().contains("PERSONAL"));
  }

  @Test
  void testGetNoteById() {
    when(noteRepository.findById("1")).thenReturn(Optional.of(noteModel));

    NoteDto dto = noteService.getNoteById("1");

    assertEquals("Test Note", dto.getTitle());
    assertEquals("Hello world hello again", dto.getText());
  }

  @Test
  void testUpdateNote() {
    when(noteRepository.findById("1")).thenReturn(Optional.of(noteModel));
    when(noteRepository.save(any(NoteModel.class))).thenReturn(noteModel);

    NoteDto dto = NoteDto.builder()
            .title("Updated Note")
            .text("Updated text")
            .tags(Set.of("IMPORTANT"))
            .build();

    NoteDto updated = noteService.update("1", dto);

    assertEquals("Updated Note", updated.getTitle());
    assertEquals("Updated text", updated.getText());
    assertTrue(updated.getTags().contains("IMPORTANT"));
  }

  @Test
  void testDeleteNote() {
    when(noteRepository.findById("1")).thenReturn(Optional.of(noteModel));
    doNothing().when(noteRepository).deleteById("1");

    noteService.delete("1");

    verify(noteRepository, times(1)).deleteById("1");
  }

  @Test
  void testGetNotesByTags() {
    Page<NoteModel> page = new PageImpl<>(Set.of(noteModel).stream().toList());

    when(noteRepository.findByTagsIn(Set.of(Tag.BUSINESS), PageRequest.of(0, 10))).thenReturn(page);

    Page<NoteDto> result = noteService.getNotesByTags(Set.of(Tag.BUSINESS), PageRequest.of(0, 10));

    assertEquals(1, result.getContent().size());
    assertEquals("Test Note", result.getContent().get(0).getTitle());
  }
}
