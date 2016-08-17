package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/annotations", produces = "application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\"")
public class WAPController {

    @Autowired
    private Anno4j anno4j;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotation(@RequestParam String uri) throws RepositoryException {
        Annotation annotation = anno4j.findByID(Annotation.class, uri);

        return annotation.getTriples(RDFFormat.JSONLD);
    }

    // TODO retrieve anno with ?uri or ?ldpath
}
