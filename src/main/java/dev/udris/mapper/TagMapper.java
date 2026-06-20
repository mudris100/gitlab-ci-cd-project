package dev.udris.mapper;

import org.springframework.stereotype.Component;

import dev.udris.dto.TagDto;
import dev.udris.entity.Tag;

@Component
public class TagMapper {

	public TagDto toTagDto(Tag tag) {
		return new TagDto(tag.getId(), tag.getName());
	}
}
