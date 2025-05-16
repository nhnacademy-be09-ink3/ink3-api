package shop.ink3.api.book.publisher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.book.publisher.dto.PublisherCreateRequest;
import shop.ink3.api.book.publisher.dto.PublisherResponse;
import shop.ink3.api.book.publisher.dto.PublisherUpdateRequest;

import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.book.publisher.exception.PublisherAlreadyExistsException;
import shop.ink3.api.book.publisher.exception.PublisherNotFoundException;
import shop.ink3.api.book.publisher.repository.PublisherRepository;
import shop.ink3.api.common.dto.PageResponse;

@Transactional
@RequiredArgsConstructor
@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    @Transactional(readOnly = true)
    public PageResponse<PublisherResponse> getPublishers(Pageable pageable) {
        Page<Publisher> publishers = publisherRepository.findAll(pageable);
        return PageResponse.from(publishers.map(PublisherResponse::from));
    }

    @Transactional(readOnly = true)
    public PublisherResponse getPublisherById(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        return PublisherResponse.from(publisher);
    }

    @Transactional(readOnly = true)
    public PublisherResponse getPublisherByName(String publisherName) {
        Publisher publisher = publisherRepository.findByName(publisherName).orElseThrow(() -> new PublisherNotFoundException(publisherName));
        return PublisherResponse.from(publisher);
    }

    public PublisherResponse createPublisher(PublisherCreateRequest publisherCreateRequest) {
        String publisherName = publisherCreateRequest.name();
        if (publisherRepository.existsByName(publisherName)) {
            throw new PublisherAlreadyExistsException(publisherName);
        }

        Publisher publisher = Publisher.builder().name(publisherName).build();
        return PublisherResponse.from(publisherRepository.save(publisher));
    }

    public PublisherResponse updatePublisher(Long publisherId, PublisherUpdateRequest publisherUpdateRequest) {
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        publisher.updatePublisherName(publisherUpdateRequest.name());
        return PublisherResponse.from(publisherRepository.save(publisher));
    }

    public void deletePublisher(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        publisherRepository.delete(publisher);
    }
}
