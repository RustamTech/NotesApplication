package org.notesApp.service;

import lombok.RequiredArgsConstructor;
import org.notesApp.dto.NoteDto;
import org.notesApp.dto.NoteSummaryDto;
import org.notesApp.entity.NoteModel;
import org.notesApp.entity.Tag;
import org.notesApp.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteDto create(NoteDto dto) {
    Set<Tag> tags = mapTags(dto.getTags());

    NoteModel note = NoteModel.builder()
            .title(dto.getTitle())
            .text(dto.getText())
            .tags(tags)
            .createdTime(LocalDateTime.now())
            .build();

    noteRepository.save(note);
    return mapToResponse(note);
  }

  public NoteDto update(String id, NoteDto dto) {
    NoteModel note = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Note not found with id " + id));

    if (dto.getTitle() != null && !dto.getTitle().isBlank()) note.setTitle(dto.getTitle());
    if (dto.getText() != null && !dto.getText().isBlank()) note.setText(dto.getText());
    if (dto.getTags() != null) note.setTags(mapTags(dto.getTags()));

    noteRepository.save(note);
    return mapToResponse(note);
  }

  public void delete(String id) {
    noteRepository.findById(id)
            .ifPresentOrElse(
                    note -> noteRepository.deleteById(id),
                    () -> { throw new RuntimeException("Note not found with id " + id); }
            );
  }

  public NoteDto getNoteById(String id) {
    NoteModel note = noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Note not found with id " + id));
    return mapToResponse(note);
  }

  public List<NoteSummaryDto> getNoteSummaries(Pageable pageable) {
    return noteRepository.findAll(pageable)
            .stream()
            .map(n -> new NoteSummaryDto(n.getId(), n.getTitle(), n.getCreatedTime()))
            .collect(Collectors.toList());
  }

  public Page<NoteDto> getNotesByTags(Set<Tag> tags, Pageable pageable) {
    Page<NoteModel> page = (tags == null || tags.isEmpty())
            ? noteRepository.findAll(pageable)
            : noteRepository.findByTagsIn(tags, pageable);

    return page.map(this::mapToResponse);
  }

  public NoteDto mapToResponse(NoteModel note) {
    Map<String, Long> wordStats = calculateWordStats(note.getText());
    Set<String> tagNames = Optional.ofNullable(note.getTags())
            .orElse(Collections.emptySet())
            .stream()
            .map(Enum::name)
            .collect(Collectors.toSet());

    return NoteDto.builder()
            .id(note.getId())
            .title(note.getTitle())
            .text(note.getText())
            .createdTime(note.getCreatedTime())
            .tags(tagNames)
            .wordStats(wordStats)
            .build();
  }

  private Map<String, Long> calculateWordStats(String text) {
    if (text == null || text.isBlank()) return Collections.emptyMap();

    return Arrays.stream(text.toLowerCase().split("\\W+"))
            .filter(s -> !s.isBlank())
            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
  }

  private Set<Tag> mapTags(Set<String> tags) {
    if (tags == null) return Collections.emptySet();

    Set<Tag> result = new HashSet<>();
    for (String t : tags) {
      try {
        result.add(Tag.valueOf(t.toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new RuntimeException("Invalid tag: " + t);
      }
    }
    return result;
  }
}
