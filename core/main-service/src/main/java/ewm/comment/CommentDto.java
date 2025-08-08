package ewm.comment;

import ru.practicum.interactionapi.dto.eventservice.EventShortDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Трансферный объект, содержащий данные о комментарии.
 */
@Builder(toBuilder = true)
@Data
public class CommentDto {
    /**
     * Уникальный идентификатор комментария
     */
    private Long id;

    /**
     * Пользователь оставивший комментарий
     */
    private UserDto user;

    /**
     * Событие по которому оставлен комментарий
     */
    private EventShortDto event;

    /**
     * Текст комментария
     */
    private String text;

    /**
     * Дата и время создания комментария
     */
    private LocalDateTime created;

    /**
     * Дата и время изменения комментария
     */
    private LocalDateTime updated;
}
