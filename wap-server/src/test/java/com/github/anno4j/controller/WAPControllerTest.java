package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.BaseWebTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.annotations.Iri;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.test.web.servlet.result.ContentResultMatchers;


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
        annotation.addBody(body);
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