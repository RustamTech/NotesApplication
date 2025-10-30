package org.notesApp.repository;

import org.notesApp.entity.NoteModel;
import org.notesApp.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Set;

public interface NoteRepository extends MongoRepository<NoteModel, String> {
 Page<NoteModel> findByTagsIn(Set<Tag> tags, Pageable pageable);
}
