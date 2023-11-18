package com.shopping.vote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.common.entity.Review;
import com.shopping.review.ReviewRepository;
import com.shopping.review.vote.VoteResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewVoteRestControllerTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testVoteNotLogin() throws Exception {
        String requestURL = "/vote_review/1/up";

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("You must login");
    }

    @Test
    @WithMockUser(username = "lehoanganhvn@gmail.com", password = "password")
    public void testVoteNonExistReview() throws Exception {
        String requestURL = "/vote_review/123/up";

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("no longer exists");
    }

    @Test
    @WithMockUser(username = "lehoanganhvn@gmail.com", password = "password")
    public void testVoteUp() throws Exception {
        Integer reviewId = 10;
        String requestURL = "/vote_review/" + 10 + "/up";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("successfully voted up");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore + 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "lehoanganhvn@gmail.com", password = "password")
    public void testUndoVoteUp() throws Exception {
        Integer reviewId = 10;
        String requestURL = "/vote_review/" + 10 + "/up";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("unvoted up");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore - 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "lehoanganhvn@gmail.com", password = "password")
    public void testVoteDown() throws Exception {
        Integer reviewId = 20;
        String requestURL = "/vote_review/" + 20 + "/down";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("successfully voted down");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore - 1, voteCountAfter);
    }

    @Test
    @WithMockUser(username = "lehoanganhvn@gmail.com", password = "password")
    public void testUndoVoteDown() throws Exception {
        Integer reviewId = 20;
        String requestURL = "/vote_review/" + 20 + "/down";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("unvoted down");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore + 1, voteCountAfter);
    }
}
