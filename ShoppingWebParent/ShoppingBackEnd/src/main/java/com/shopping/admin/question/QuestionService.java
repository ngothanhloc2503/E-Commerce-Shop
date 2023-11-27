package com.shopping.admin.question;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.common.entity.Question;
import com.shopping.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class QuestionService {
    public static final int QUESTION_PER_PAGE = 5;

    @Autowired
    private QuestionRepository repository;

    public List<Question> listAll() {
        return repository.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, QUESTION_PER_PAGE, repository);
    }

    public Question findById(Integer id) {
        return repository.findById(id).get();
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public void updateQuestionApprovalStatus(Integer id, boolean status) {
        repository.updateQuestionApprovalStatus(id, status);
    }

    public void save(Question questionInForm, User user) {
        Question questionInDB = findById(questionInForm.getId());
        if (questionInForm.getAnswerContent() != null && !questionInForm.getAnswerContent().isBlank()) {
            if (!questionInForm.getAnswerContent().equals(questionInDB.getAnswerContent())) {
                questionInDB.setAnswerContent(questionInForm.getAnswerContent());
                questionInDB.setAnswerTime(new Date());
                questionInDB.setAnswerer(user);
                questionInDB.setAnswered(true);
            }
        } else {
            questionInDB.setAnswerContent(null);
            questionInDB.setAnswerTime(null);
            questionInDB.setAnswerer(null);
            questionInDB.setAnswered(false);
        }

        questionInDB.setApprovalStatus(questionInForm.isApprovalStatus());

        repository.save(questionInDB);
    }
}
