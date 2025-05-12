package shop.ink3.api.books.tags.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.books.tags.dto.TagCreateRequest;
import shop.ink3.api.books.tags.dto.TagResponse;
import shop.ink3.api.books.tags.entity.Tags;
import shop.ink3.api.books.tags.exception.TagAlreadyExistsException;
import shop.ink3.api.books.tags.exception.TagNotFoundException;
import shop.ink3.api.books.tags.repository.TagsRepository;


@RequiredArgsConstructor
@Service
public class TagsService {
    private final TagsRepository tagsRepository;

    public List<TagResponse> getTags() {
        return tagsRepository.findAll()
                .stream()
                .map(TagResponse::from)
                .toList();
    }

    public TagResponse getTagById(Long tagId) {
        Tags tags = tagsRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));
        return TagResponse.from(tags);
    }

    public TagResponse getTagByName(String tagName) {
        Tags tags = tagsRepository.findByName(tagName).orElseThrow(() -> new TagNotFoundException(tagName));
        return TagResponse.from(tags);
    }

    @Transactional
    public TagResponse createTag(TagCreateRequest tagCreateRequest) {
        String tagName = tagCreateRequest.name();
        if (tagsRepository.existsByName(tagName)) {
            throw new TagAlreadyExistsException(tagName);
        }
        Tags tags = Tags.builder().name(tagCreateRequest.name()).build();
        return TagResponse.from(tagsRepository.save(tags));
    }

    @Transactional
    public void deleteTag(Long tagId) {
        Tags tags = tagsRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));
        tagsRepository.delete(tags);
    }
}
