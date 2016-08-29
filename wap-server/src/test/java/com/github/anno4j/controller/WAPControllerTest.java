package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.Body;
import com.github.anno4j.BaseWebTest;
import com.github.anno4j.model.impl.body.TextualBody;
import com.github.anno4j.model.namespaces.OADM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.test.web.servlet.result.ContentResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
public class WAPControllerTest extends BaseWebTest {

    @Autowired
    private Anno4j anno4j;

    private String annotationURI;

    private final static String JSONLD_ACCEPT_HEADER = "application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\"";

    private final static String ANNO_WITHOUT_PREFIX = "annowithoutprefix";
    private final static String CUSTOM_PREFIX = "urn:custom";
    private final static String ANNO_WITH_PREFIX = "annowithprefix";

    private Annotation annotationByPathWithoutPrefix;
    private Annotation annotationByPathWithPrefix;

    @Before
    public void initAnnotations() throws Exception {
        TextualBody body = this.anno4j.createObject(TextualBody.class);
        body.setValue("testvalue");

        Annotation annotation = anno4j.createObject(Annotation.class);
        annotation.addBody(body);
        annotationURI = annotation.getResourceAsString();

        annotationByPathWithoutPrefix = this.anno4j.createObject(Annotation.class, (Resource) new URIImpl("urn:anno4j:" + ANNO_WITHOUT_PREFIX));

        annotationByPathWithPrefix = this.anno4j.createObject(Annotation.class, (Resource) new URIImpl(CUSTOM_PREFIX + ":" + ANNO_WITH_PREFIX));
    }

    @Test
    public void testGetAnnotations() throws Exception {
        ContentResultMatchers content = content();
        mockMvc.perform(get("/annotations")
                .header("Accept", JSONLD_ACCEPT_HEADER)
                .param("uri", annotationURI))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""));
    }

    @Test
    public void testGetAnnotationByPathWitoutPrefix() throws Exception {
        mockMvc.perform(get("/annotations/" + ANNO_WITHOUT_PREFIX)
                .header("Accept", JSONLD_ACCEPT_HEADER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""))
                .andExpect(jsonPath("$[0].@id", is(this.annotationByPathWithoutPrefix.getResourceAsString())))
                .andExpect(jsonPath("$[0].@type.[0]", is(OADM.ANNOTATION)));
    }

    @Test
    public void testGetAnnotationByPathWithPrefix() throws Exception {
        mockMvc.perform(get("/annotations/" + ANNO_WITH_PREFIX)
                .param("prefix", CUSTOM_PREFIX)
                .header("Accept", JSONLD_ACCEPT_HEADER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\""))
                .andExpect(jsonPath("$[0].@id", is(this.annotationByPathWithPrefix.getResourceAsString())))
                .andExpect(jsonPath("$[0].@type.[0]", is(OADM.ANNOTATION)));
    }

    @Test
    public void testAnnotationNotFound() throws Exception {
        mockMvc.perform(get("/annotations/someanno")
                .header("Accept", JSONLD_ACCEPT_HEADER))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testWrongContentNegotiation() throws Exception {
        mockMvc.perform(get("/annotations/someanno")
                .header("Accept", "text/plain"))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }
}