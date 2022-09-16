package ru.senla.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.senla.exception.EmptyResponseException;
import ru.senla.model.Comment;
import ru.senla.model.Post;
import ru.senla.model.User;
import ru.senla.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Optional<Comment> findById(long id) {
        log.info("Find comment by id = {}", id);
        return commentRepository.findById(id);
    }

    public List<Comment> findAllByUser(User user) throws EmptyResponseException {
        log.info("Find all comments by user = {}", user.getUsername());
        List<Comment> comments = commentRepository.findAllByUser(user);
        if (comments.isEmpty())
            throw new EmptyResponseException("Данный пользователь еще не написал ни одного комментария");
        return comments;
    }

    public List<Comment> findAllByPost(Post post) throws EmptyResponseException {
        log.info("Find all comments under post = {}", post.getId());
        List<Comment> comments = commentRepository.findAllByPost(post);
        if (comments.isEmpty())
            throw new EmptyResponseException("Под данным не объявлением еще не оставляли комментариев");
        return comments;
    }

    @Transactional
    public void create(Comment comment) {
        log.info("Write comment under post = {}", comment.getPost().getId());
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Comment comment) {
        log.info("Delete comment with id = {}", comment.getId());
        commentRepository.delete(comment);
    }
}
