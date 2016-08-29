package com.github.anno4j.controller;

import com.github.anno4j.Anno4j;
import com.github.anno4j.exception.AnnotationNotFoundException;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.querying.QueryService;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/annotations", produces = "application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\"")
public class WAPController {

    @Autowired
    private Anno4j anno4j;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotationByParam(@RequestParam String uri) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        return findAnnotation(uri).getTriples(RDFFormat.JSONLD);
    }

    @RequestMapping(value = "/{annoId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotation(@PathVariable String annoId, @RequestParam(value = "prefix", defaultValue = "urn:anno4j") String prefix) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        String annotationID = prefix + ":" + annoId;

        return findAnnotation(annotationID).getTriples(RDFFormat.JSONLD);

    }

    private Annotation findAnnotation(String annotationId) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();
        qs.addCriteria(".", annotationId);

        List<Annotation> result = qs.execute(Annotation.class);

        if(result.isEmpty()) {
            throw new AnnotationNotFoundException(annotationId);
        } else {
            return result.get(0);
        }
    }



    // TODO retrieve anno with ?uri or ?ldpath
}
