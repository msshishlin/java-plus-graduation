package ru.practicum.compilationservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilationservice.model.Compilation;
import ru.practicum.interactionapi.dto.compilationservice.CompilationDto;
import ru.practicum.interactionapi.dto.compilationservice.CreateCompilationDto;
import ru.practicum.interactionapi.openfeign.EventServiceClient;

import java.util.Collection;

/**
 * Маппер для сущности подборки событий.
 */
@Component
@RequiredArgsConstructor
public class CompilationMapper {
    /**
     * Клиент сервиса для работы с событиями.
     */
    private final EventServiceClient eventServiceClient;

    /**
     * Преобразовать трансферный объект, содержащий данные для создания подборки событий, в объект подборки событий.
     *
     * @param createCompilationDto трансферный объект, содержащий данные для создания подборки событий.
     * @return объект подборки событий.
     */
    public Compilation mapToCompilation(CreateCompilationDto createCompilationDto) {
        return Compilation.builder()
                .title(createCompilationDto.getTitle())
                .events(createCompilationDto.getEvents())
                .pinned(createCompilationDto.getPinned())
                .build();
    }

    /**
     * Преобразовать объект подборки событий в трансферный объект, содержащий данные о подборке событий.
     *
     * @param compilation объект подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     */
    public CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents() != null ? eventServiceClient.getEvents(compilation.getEvents()) : null)
                .pinned(compilation.isPinned())
                .build();
    }

    /**
     * Преобразовать коллекцию объектов подборок событий в коллекцию трансферных объектов, содержащих данные о подборках событий.
     *
     * @param compilations коллекция объектов подборок событий.
     * @return коллекция трансферных объектов, содержащих данные о подборках событий.
     */
    public Collection<CompilationDto> mapToCompilationDtoCollection(Collection<Compilation> compilations) {
        return compilations.stream().map(this::mapToCompilationDto).toList();
    }
}
