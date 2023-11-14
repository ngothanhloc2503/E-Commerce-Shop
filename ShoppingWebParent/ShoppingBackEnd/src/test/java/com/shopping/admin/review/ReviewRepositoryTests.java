package com.shopping.admin.review;

import com.shopping.admin.review.repository.ReviewRepository;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Review;
import com.shopping.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository repository;

    @Test
    public void testCreateReview() {
        Integer productId = 1;
        Product product = new Product(productId);

        Integer customerId = 1;
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewTime(new Date());
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setRating(5);

        Review savedReview = repository.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testListReviews() {
        List<Review> listReviews = repository.findAll();

        assertThat(listReviews.size()).isGreaterThan(0);

        listReviews.forEach(System.out::println);
    }

    @Test
    public void testGetReview() {
        Integer id = 1;
        Review review = repository.findById(id).get();

        assertThat(review).isNotNull();

        System.out.println(review);
    }

    @Test
    public void testUpdateReview() {
        Integer id = 1;
        String headline = "An awesome camera at an awesome price";
        String comment = "Overall great camera and is highly capable...";

        Review review = repository.findById(id).get();
        review.setHeadline(headline);
        review.setComment(comment);

        Review updatedReview = repository.save(review);

        assertThat(updatedReview.getHeadline()).isEqualTo(headline);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    public void testDeleteReview() {
        Integer id = 2;
        repository.deleteById(id);

        Optional<Review> findById = repository.findById(id);

        assertThat(findById).isNotPresent();
    }
}
