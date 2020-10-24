package com.etn319.dao.jpa;

import com.etn319.dao.api.CommentDao;
import com.etn319.model.Book;
import com.etn319.model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Comment DAO JPA")
@Import(CommentDaoJpaImpl.class)
class CommentDaoJpaTest {
    private static final int EXPECTED_NUMBER_OF_COMMENTS_BY_BOOK = 2;
    private static final int EXPECTED_NUMBER_OF_COMMENTS_BY_COMMENTER = 3;
    private static final String COMMENTER = "Commenter 3";
    private static final String COMMENT_TEXT = "Test text";
    private static final Book BOOK = new Book(1L, "Martin Eden", null, null);

    @Autowired
    private CommentDao dao;

    @Autowired
    private TestEntityManager em;

    @Test
    void getCommentsByBook() {
        List<String> expectedComments = List.of("10/10, pishi esche", "Very nice");
        List<Comment> comments = dao.getByBook(BOOK);
        assertThat(comments)
                .hasSize(EXPECTED_NUMBER_OF_COMMENTS_BY_BOOK)
                .extracting(Comment::getText)
                .containsExactlyElementsOf(expectedComments);
    }

    @Test
    void getCommentsByCommenterName() {
        List<String> expectedComments = List.of("Good", "Super", "Very nice");
        List<Comment> comments = dao.getByCommenterName(COMMENTER);
        assertThat(comments)
                .hasSize(EXPECTED_NUMBER_OF_COMMENTS_BY_COMMENTER)
                .extracting(Comment::getText)
                .containsExactlyElementsOf(expectedComments);
    }

    @Test
    void saveNewComment() {
        var comment = new Comment();
        comment.setCommenter(COMMENTER);
        comment.setText(COMMENT_TEXT);
        comment.setBook(BOOK);
        var savedComment = dao.save(comment);

        var foundComment = em.find(Comment.class, savedComment.getId());
        assertThat(foundComment).isEqualToComparingFieldByField(savedComment);
    }

    @Test
    void deleteById() {
        var comment = em.find(Comment.class, 1L);
        em.detach(comment);
        assertThat(comment).isNotNull();

        dao.deleteById(1L);
        var deletedComment = em.find(Comment.class, 1L);
        assertThat(deletedComment).isNull();
    }

    @Test
    void getById() {
        var commentFromDao = dao.getById(1L);
        var commentFromEm = em.find(Comment.class, 1L);

        assertThat(commentFromDao)
                .isPresent()
                .get()
                .isEqualToComparingFieldByField(commentFromEm);
    }
}
