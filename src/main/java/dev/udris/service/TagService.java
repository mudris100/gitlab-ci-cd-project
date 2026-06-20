package dev.udris.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.udris.dto.TagDto;
import dev.udris.entity.Tag;
import dev.udris.exception.NotFoundException;
import dev.udris.mapper.TagMapper;
import dev.udris.repository.TagRepository;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper mapper;

    public TagService(TagRepository tagRepository, TagMapper mapper) {
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    public List<TagDto> findAll() {
        List<Tag> tags = tagRepository.findAllOrderByName();
        return tags.stream().map(mapper::toTagDto).collect(Collectors.toList());
    }

    public Tag findByIdOrThrow(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new NotFoundException("Tag", "id", id));

    }

    public TagDto findTagDtoById(Long id) {
        return mapper.toTagDto(findByIdOrThrow(id));
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name).orElseThrow(() -> new NotFoundException("Tag", "name", name));
    }

    public Tag findByNameOrCreate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        String trimmedName = name.trim();
        if (trimmedName.length() > 50) {
            throw new IllegalArgumentException("Tag name cannot exceed 50 characters");
        }

        try {
            return tagRepository.findByName(trimmedName).orElseGet(() -> {
                try {
                    return tagRepository.save(new Tag(trimmedName));
                } catch (Exception e) {
                    return tagRepository.findByName(trimmedName)
                            .orElseThrow(() -> new RuntimeException("Failed to create tag: " + trimmedName, e));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error processing tag: " + trimmedName, e);
        }
    }

    public List<TagDto> findByNameContaining(String name) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(name);
        return tags.stream().map(mapper::toTagDto).collect(Collectors.toList());
    }

    public List<TagDto> findMostUsedTags() {
        List<Tag> tags = tagRepository.findMostUsedTags();
        return tags.stream().map(mapper::toTagDto).collect(Collectors.toList());
    }

    public Tag createTag(String name) {
        if (tagRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Tag with name '" + name + "' already exists");
        }
        return tagRepository.save(new Tag(name.trim()));
    }

    public TagDto updateTag(Long id, String name) {
        Tag tag = findByIdOrThrow(id);
        tag.setName(name.trim());
        tagRepository.save(tag);
        return mapper.toTagDto(tag);
    }

    public void deleteTag(Long id) {
        Tag tag = findByIdOrThrow(id);
        tagRepository.delete(tag);
    }

    public Set<Tag> parseTagsFromString(String tagsString) {
        try {
            if (tagsString == null || tagsString.trim().isEmpty()) {
                return new HashSet<>();
            }

            System.out.println("Parsing tags from string: '" + tagsString + "'");

            Set<Tag> tags = Arrays.stream(tagsString.split(",")).map(String::trim).filter(tag -> !tag.isEmpty())
                    .peek(tag -> System.out.println("Processing tag: '" + tag + "'")).map(this::findByNameOrCreate)
                    .collect(Collectors.toSet());

            System.out.println("Successfully parsed " + tags.size() + " tags");
            return tags;
        } catch (Exception e) {
            System.err.println("Error parsing tags from string: '" + tagsString + "'");
            e.printStackTrace();
            throw new RuntimeException("Error parsing tags: " + e.getMessage(), e);
        }
    }

    public String tagsToString(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
    }
}