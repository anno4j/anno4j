package com.github.anno4j.controller;

import com.anno4j.anno4j.Anno4j;
import com.anno4j.anno4j.model.Annotation;
import com.anno4j.anno4j.model.Body;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.anno4j.Application;
import com.github.anno4j.BaseWebTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
public class WAPControllerTest extends BaseWebTest {

    @Autowired
    private Anno4j anno4j;

    private String annotationURI;

    @Before
    public void initAnnotations() throws Exception {
        TestWAPBody body = anno4j.createObject(TestWAPBody.class);
        body.setValue("Example Value");

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.setBody(body);
        annotationURI = annotation.getResourceAsString();
    }

    @Test
    public void getAnnotations() throws Exception {

        ContentResultMatchers content = content();
        mockMvc.perform(get("/annotations")
                .param("uri", annotationURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""));
    }


    @Iri("http://www.example.com/schema#WAPBody")
    public static interface TestWAPBody extends Body {
        @Iri("http://www.wapexample.com/schema#value")
        String getValue();

        @Iri("http://www.wapexample.com/schema#value")
        void setValue(String value);
    }
}