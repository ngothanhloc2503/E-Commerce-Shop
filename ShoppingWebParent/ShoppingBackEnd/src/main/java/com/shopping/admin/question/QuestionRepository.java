package com.shopping.admin.question;

import com.shopping.admin.paging.SearchRepository;
import com.shopping.common.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepository extends JpaRepository<Question, Integer>,
        SearchRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.answerContent LIKE %?1% OR q.questionContent LIKE %?1% OR q.product.name LIKE %?1%"
            + " OR CONCAT(q.asker.firstName, ' ', q.asker.lastName) LIKE %?1%")
    public Page<Question> findAll(String keyword, Pageable pageable);

    @Query("UPDATE Question q SET q.approvalStatus = ?2 WHERE q.id = ?1")
    @Modifying
    void updateQuestionApprovalStatus(Integer id, boolean status);
}
