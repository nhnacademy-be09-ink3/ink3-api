package shop.ink3.api.book.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.tag.dto.TagCreateRequest;
import shop.ink3.api.book.tag.dto.TagResponse;
import shop.ink3.api.book.tag.dto.TagUpdateRequest;
import shop.ink3.api.book.tag.entity.Tag;
import shop.ink3.api.book.tag.exception.TagAlreadyExistsException;
import shop.ink3.api.book.tag.exception.TagNotFoundException;
import shop.ink3.api.book.tag.repository.TagRepository;
import shop.ink3.api.common.dto.PageResponse;

@Transactional
@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public PageResponse<TagResponse> getTags(Pageable pageable) {
        Page<Tag> tags = tagRepository.findAll(pageable);
        return PageResponse.from(tags.map(TagResponse::from));
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));
        return TagResponse.from(tag);
    }

    @Transactional(readOnly = true)
    public TagResponse getTagByName(String tagName) {
        Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new TagNotFoundException(tagName));
        return TagResponse.from(tag);
    }

    public TagResponse createTag(TagCreateRequest tagCreateRequest) {
        String tagName = tagCreateRequest.name();
        if (tagRepository.existsByName(tagName)) {
            throw new TagAlreadyExistsException(tagName);
        }
        Tag tag = Tag.builder().name(tagCreateRequest.name()).build();
        return TagResponse.from(tagRepository.save(tag));
    }

    public TagResponse updateTag(Long tagId, TagUpdateRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));

        tag.updateTagName(request.name());
        Tag updated = tagRepository.save(tag);

        return TagResponse.from(updated);
    }

    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));
        tagRepository.delete(tag);
    }
}
