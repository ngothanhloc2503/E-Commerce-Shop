package com.shopping.common.entity;

import com.shopping.common.entity.product.Product;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review extends IdBasedEntity {

    @Column(length = 128, nullable = false)
    private String headline;

    @Column(length = 300, nullable = false)
    private String comment;

    private int rating;

    private int votes;

    @Column(nullable = false)
    private Date reviewTime;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Transient
    private boolean upVotedByCurrentCustomer;

    @Transient
    private boolean downVotedByCurrentCustomer;

    public Review() {
    }

    public Review(String headline, String comment, int rating, Date reviewTime, Product product, Customer customer) {
        this.headline = headline;
        this.comment = comment;
        this.rating = rating;
        this.reviewTime = reviewTime;
        this.product = product;
        this.customer = customer;
    }

    public Review(Integer reviewId) {
        this.id = reviewId;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean isUpVotedByCurrentCustomer() {
        return upVotedByCurrentCustomer;
    }

    public void setUpVotedByCurrentCustomer(boolean upVotedByCurrentCustomer) {
        this.upVotedByCurrentCustomer = upVotedByCurrentCustomer;
    }

    public boolean isDownVotedByCurrentCustomer() {
        return downVotedByCurrentCustomer;
    }

    public void setDownVotedByCurrentCustomer(boolean downVotedByCurrentCustomer) {
        this.downVotedByCurrentCustomer = downVotedByCurrentCustomer;
    }

    @Override
    public String toString() {
        return "Review{id=" + id +
                ", headline='" + headline + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", reviewTime=" + reviewTime +
                ", product=" + product.getShortName() +
                ", customer=" + customer.getFullName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
