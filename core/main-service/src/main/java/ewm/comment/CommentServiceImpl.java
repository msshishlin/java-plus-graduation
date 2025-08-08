package ewm.comment;

import ewm.exception.IncorrectlyException;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.openfeign.UserServiceClient;
import ru.practicum.interactionapi.pageable.PageOffset;

import java.util.Collection;

/**
 * Сервис для сущности "Комментарий".
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    /**
     * Хранилище данных для сущности "Комментарий".
     */
    private final CommentRepository commentRepository;

    /**
     * Клиент для сервиса управления пользователями.
     */
    private final UserServiceClient userServiceClient;

    /**
     * Поиск комментариев по переданным параметрам
     *
     * @param params объект для передачи параметров в сервис комментария
     * @return трансферный объект, содержащий данные о комментарии
     */
    @Override
    public Collection<CommentDto> getComments(CommentParams params) {
        Sort sort = switch (params.getSort()) {
            case ASC -> Sort.by(Sort.Order.asc("created"));
            case DESC -> Sort.by(Sort.Order.desc("created"));
        };
        Pageable pageable = PageOffset.of(params.getFrom(), params.getSize(), sort);
        if (params.getUserId() == null && params.getEventId() == null) {
            throw new IncorrectlyException("Необходимо передать или userId или eventId или оба вместе");
        } else if (params.getUserId() != null && params.getEventId() == null) {
            return CommentMapper.INSTANCE.toCommentDtoCollection(
                    commentRepository.findByUserId(params.getUserId(), pageable).getContent());
        } else if (params.getUserId() == null) {
            return CommentMapper.INSTANCE.toCommentDtoCollection(
                    commentRepository.findByEventId(params.getEventId(), pageable).getContent());
        } else {
            return CommentMapper.INSTANCE.toCommentDtoCollection(
                    commentRepository.findByUserIdAndEventId(params.getUserId(), params.getEventId(), pageable).getContent());
        }
    }

    /**
     * Добавить новый комментарий
     *
     * @param params объект для передачи параметров в сервис комментария
     * @return трансферный объект, содержащий данные о комментарии
     */
    @Override
    @Transactional
    public CommentDto createComment(CommentParams params) {
        UserDto userShortDto = userServiceClient.findUserById(params.getUserId());

        CommentMapperParams mapperParams = CommentMapperParams.builder()
                .createCommentDto(params.getCreateCommentDto())
                .userId(userShortDto.getId())
                .eventId(params.getEventId())
                .build();
        return CommentMapper.INSTANCE.toCommentDto(
                commentRepository.save(CommentMapper.INSTANCE.toCommentCreate(mapperParams)));
    }

    /**
     * Изменить комментарий
     *
     * @param params объект для передачи параметров в сервис комментария
     * @return трансферный объект, содержащий данные о комментарии
     */
    @Override
    @Transactional
    public CommentDto updateComment(CommentParams params) {
        UserDto userShortDto = userServiceClient.findUserById(params.getUserId());

        Comment comment = findById(params.getCommentId());

        if (!params.getEventId().equals(comment.getEventId())) {
            throw new IncorrectlyException(String.format("Комментарий не относится к событию с id = %d", params.getEventId()));
        }
        if (!userShortDto.getId().equals(comment.getUserId())) {
            throw new IncorrectlyException(String.format("Комментарий не принадлежит пользователю с id = %d", userShortDto.getId()));
        }

        CommentMapperParams mapperParams = CommentMapperParams.builder()
                .updateCommentDto(params.getUpdateCommentDto())
                .userId(userShortDto.getId())
                .eventId(params.getEventId())
                .comment(comment)
                .build();
        return CommentMapper.INSTANCE.toCommentDto(
                commentRepository.save(CommentMapper.INSTANCE.toCommentUpdate(mapperParams)));
    }

    /**
     * Удалить комментарий
     *
     * @param params объект для передачи параметров в сервис комментария
     */
    @Override
    @Transactional
    public void deleteComment(CommentParams params) {
        UserDto userShortDto = userServiceClient.findUserById(params.getUserId());

        Comment comment = findById(params.getCommentId());
        if (!params.getEventId().equals(comment.getEventId())) {
            throw new IncorrectlyException(String.format("Комментарий не относится к событию с id = %d", params.getEventId()));
        }
        if (!userShortDto.getId().equals(comment.getUserId())) {
            throw new IncorrectlyException(String.format("Комментарий не принадлежит пользователю с id = %d", userShortDto.getId()));
        }
        commentRepository.delete(comment);
    }

    /**
     * Поиск комментария
     *
     * @param commentId ИД комментария
     * @return объект сущности "Комментарий"
     */
    private Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден комментарий с id = %d", commentId)));
    }
}
