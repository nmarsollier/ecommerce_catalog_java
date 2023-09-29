package com.catalog.rest;

import com.catalog.CatalogApplication;
import com.catalog.CommonMocks;
import com.catalog.article.ArticleRepository;
import com.catalog.utils.errors.NotFoundError;
import com.catalog.utils.gson.GsonTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = CatalogApplication.class)
@AutoConfigureMockMvc
public class GetArticleIdTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleRepository repository;

    @Test
    public void testGetArticleIdSuccess() throws Exception {
        Mockito.when(repository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(CommonMocks.articleExample));

        String body = mvc.perform(get("/v1/articles/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(CommonMocks.articleExample.data().toJson(), body);
    }

    @Test
    public void testGetArticleIdNotFound() throws Exception {
        Mockito.when(repository.findById(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        String body = mvc.perform(get("/v1/articles/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn().getResponse().getContentAsString();

        Assertions.assertEquals(GsonTools.toJson(new NotFoundError("articleId").message()), body);
    }
}