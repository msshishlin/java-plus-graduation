package ru.practicum.compilationservice.service;

import ru.practicum.interactionapi.dto.compilationservice.CompilationDto;
import ru.practicum.interactionapi.dto.compilationservice.CreateCompilationDto;
import ru.practicum.interactionapi.dto.compilationservice.UpdateCompilationDto;
import ru.practicum.interactionapi.exception.compilationservice.CompilationNotFoundException;

import java.util.Collection;

/**
 * Контракт сервиса для сущности "Подборка событий".
 */
public interface CompilationService {

    /**
     * Добавить новую подборку событий.
     *
     * @param createCompilationDto трансферный объект, содержащий данные для создания подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     */
    CompilationDto createCompilation(CreateCompilationDto createCompilationDto);

    /**
     * Получить коллекцию подборок событий.
     *
     * @param pinned признак, необходимо ли извлекать только подборки событий, закрепленные на главной странице сайта.
     * @param from   количество подборок событий, которое необходимо пропустить.
     * @param size   количество подборок событий, которое необходимо извлечь.
     * @return коллекция трансферных объектов, содержащих данные о подборках событий.
     */
    Collection<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    /**
     * Получить подборку событий по её идентификатору.
     *
     * @param compilationId идентификатор подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     * @throws CompilationNotFoundException подборка событий с идентификатором {@code compilationId} не найдена.
     */
    CompilationDto getCompilationById(Long compilationId) throws CompilationNotFoundException;

    /**
     * Обновить подборку событий.
     *
     * @param compilationId        идентификатор подборки событий.
     * @param updateCompilationDto трансферный объект, содержащий данные для обновления подборки событий.
     * @return трансферный объект, содержащий данные о подборке событий.
     * @throws CompilationNotFoundException подборка событий с идентификатором {@code compilationId} не найдена.
     */
    CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto updateCompilationDto) throws CompilationNotFoundException;

    /**
     * Удалить подборку событий.
     *
     * @param compilationId идентификатор подборки событий.
     * @throws CompilationNotFoundException подборка событий с идентификатором {@code compilationId} не найдена.
     */
    void deleteCompilation(Long compilationId) throws CompilationNotFoundException;
}
