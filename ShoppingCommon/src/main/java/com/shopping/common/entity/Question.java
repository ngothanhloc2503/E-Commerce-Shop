package com.shopping.common.entity;

import com.shopping.common.entity.product.Product;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "questions")
public class Question extends IdBasedEntity {

    @Column(length = 300, nullable = false)
    private String questionContent;

    @Column(nullable = false)
    private Date askTime;

    @Column(length = 300)
    private String answerContent;

    private Date answerTime;

    private boolean approvalStatus;

    private int votes;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer asker;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User answerer;

    @Transient
    private boolean answered;

    public Question() {
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public Date getAskTime() {
        return askTime;
    }

    public void setAskTime(Date askTime) {
        this.askTime = askTime;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getAsker() {
        return asker;
    }

    public void setAsker(Customer asker) {
        this.asker = asker;
    }

    public User getAnswerer() {
        return answerer;
    }

    public void setAnswerer(User answerer) {
        this.answerer = answerer;
    }

    public boolean isAnswered() {
        return answerer != null;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionContent='" + questionContent + '\'' +
                ", askTime=" + askTime +
                ", answerContent='" + answerContent + '\'' +
                ", answerTime=" + answerTime +
                ", approvalStatus=" + approvalStatus +
                ", votes=" + votes +
                ", product=" + product.getShortName() +
                ", asker=" + asker.getFullName() +
                ", answerer=" + answerer.getFullName() +
                '}';
    }


}
