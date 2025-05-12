package shop.ink3.api.books.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.books.dto.PublisherCreateRequest;
import shop.ink3.api.books.dto.PublisherResponse;
import shop.ink3.api.books.dto.PublisherUpdateRequest;
import shop.ink3.api.books.entity.Publishers;
import shop.ink3.api.books.exception.PublisherAlreadyExistsException;
import shop.ink3.api.books.exception.PublisherNotFoundException;
import shop.ink3.api.books.repository.PublishersRepository;

@RequiredArgsConstructor
@Service
public class PublishersService {

    private final PublishersRepository publishersRepository;

    public List<PublisherResponse> getPublishers() {
        return publishersRepository.findAll()
                .stream()
                .map(PublisherResponse::from)
                .toList();
    }

    public PublisherResponse getPublisherById(Long publisherId) {
        Publishers publishers = publishersRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        return PublisherResponse.from(publishers);
    }

    public PublisherResponse getPublisherByName(String publisherName) {
        Publishers publishers = publishersRepository.findByName(publisherName).orElseThrow(() -> new PublisherNotFoundException(publisherName));
        return PublisherResponse.from(publishers);
    }

    @Transactional
    public PublisherResponse createPublisher(PublisherCreateRequest publisherCreateRequest) {
        String publisherName = publisherCreateRequest.name();
        if (publishersRepository.existsByName(publisherName)) {
            throw new PublisherAlreadyExistsException(publisherName);
        }

        Publishers publishers = Publishers.builder().name(publisherName).build();
        return PublisherResponse.from(publishersRepository.save(publishers));
    }

    @Transactional
    public PublisherResponse updatePublisher(Long publisherId, PublisherUpdateRequest publisherUpdateRequest) {
        Publishers publishers = publishersRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        publishers.setName(publisherUpdateRequest.name());
        return PublisherResponse.from(publishersRepository.save(publishers));
    }

    @Transactional
    public void deletePublisher(Long publisherId) {
        Publishers publisher = publishersRepository.findById(publisherId).orElseThrow(() -> new PublisherNotFoundException(publisherId));
        publishersRepository.delete(publisher);
    }
}
