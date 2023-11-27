package com.shopping.admin.question;

import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Question;
import com.shopping.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class QuestionRepositoryTests {

    @Autowired
    private QuestionRepository repository;

    @Test
    public void testCreateQuestion() {
        Integer productId = 1;
        Product product = new Product(productId);

        Integer customerId = 1;
        Customer customer = new Customer(customerId);

        Question question = new Question();
        question.setProduct(product);
        question.setAsker(customer);
        question.setAskTime(new Date());
        question.setQuestionContent("Can this run 3d modeling program blender?");

        Question savedQuestion = repository.save(question);

        assertThat(savedQuestion).isNotNull();
        assertThat(savedQuestion.getId()).isGreaterThan(0);
    }
}
