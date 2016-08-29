package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
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
    public String getAnnotationByParam(@RequestParam String uri) throws RepositoryException {
        Annotation annotation = anno4j.findByID(Annotation.class, uri);

        return annotation.getTriples(RDFFormat.JSONLD);
    }

    @RequestMapping(value = "/{annoId}", method = RequestMethod.GET)
    public String getAnnotation(@PathVariable String annoId, @RequestParam(value = "prefix", defaultValue = "urn:anno4j") String prefix) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        String annotationID = prefix + ":" + annoId;

        // findByID would be the way to go, however it has a strange behaviour, when no Annotation is found
//        Annotation annotation = this.anno4j.findByID(Annotation.class, annotationID);

        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", annotationID);

        Annotation annotation = qs.execute(Annotation.class).get(0);

        return annotation.getTriples(RDFFormat.JSONLD);
    }



    // TODO retrieve anno with ?uri or ?ldpath
}
