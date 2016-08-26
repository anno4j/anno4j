package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.BaseWebTest;
import com.github.anno4j.model.impl.body.TextualBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
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

    private final static String ANNO_WITHOUT_PREFIX = "annowithoutprefix";
    private final static String CUSTOM_PREFIX = "urn:custom";
    private final static String ANNO_WITH_PREFIX = "annowithprefix";

    @Before
    public void initAnnotations() throws Exception {
        TextualBody body = this.anno4j.createObject(TextualBody.class);
        body.setValue("testvalue");

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.addBody(body);
        annotationURI = annotation.getResourceAsString();

        // getAnnotationByPathWitoutPrefix()
        Annotation annotation2 = this.anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:anno4j:" + ANNO_WITHOUT_PREFIX));

//        getAnnotationByPathWithPrefix()
        Annotation annotation3 = this.anno4j.createObject(Annotation.class, (Resource) new URIImpl(CUSTOM_PREFIX + ":" + ANNO_WITH_PREFIX));
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

    @Test
    public void getAnnotationByPathWitoutPrefix() throws Exception {
        mockMvc.perform(get("/annotations/" + ANNO_WITHOUT_PREFIX))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""));
    }

    @Test
    public void getAnnotationByPathWithPrefix() throws Exception {
        mockMvc.perform(get("/annotations/" + ANNO_WITH_PREFIX)
                .param("prefix", CUSTOM_PREFIX))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""));
    }
}