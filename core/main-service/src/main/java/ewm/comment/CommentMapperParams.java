package ewm.comment;

import lombok.Builder;
import lombok.Data;

/**
 * Класс для передачи параметров в mapper комментария
 */
@Builder(toBuilder = true)
@Data
public class CommentMapperParams {
    /**
     * Комментарий (используется при изменении комментария)
     */
    private Comment comment;

    /**
     * Пользователь, оставивший комментарий (используется при добавлении и изменении комментария)
     */
    private Long userId;

    /**
     * Событие (используется при добавлении и изменении комментария)
     */
    private Long eventId;

    /**
     * Трансферный объект, содержащий данные для добавления комментария.
     */
    private CreateCommentDto createCommentDto;

    /**
     * Трансферный объект, содержащий данные для изменения комментария.
     */
    private UpdateCommentDto updateCommentDto;
}
