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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/annotations")
public class WAPController {

    @Autowired
    private Anno4j anno4j;

    @RequestMapping(method = RequestMethod.GET, produces = "application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\"")
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotationByParamJson(@RequestParam String uri) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return getAnnotationByParam(uri, RDFFormat.JSONLD);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/x-turtle")
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotationByParamTurtle(@RequestParam String uri) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return getAnnotationByParam(uri, RDFFormat.TURTLE);
    }

    private String getAnnotationByParam(String uri, RDFFormat format) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return findAnnotation(uri).getTriples(format);
    }

    @RequestMapping(value = "/{annoId}", method = RequestMethod.GET, produces = "application/ld+json;profile=\"http://www.w3.org/ns/anno.jsonld\"")
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotationByPathJson(@PathVariable String annoId, @RequestParam(value = "prefix", defaultValue = "urn:anno4j") String prefix) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return getAnnotationByPath(annoId, prefix, RDFFormat.JSONLD);
    }

    @RequestMapping(value = "/{annoId}", method = RequestMethod.GET, produces = "application/x-turtle")
    @ResponseStatus(value = HttpStatus.OK)
    public String getAnnotationByPathTurtle(@PathVariable String annoId, @RequestParam(value = "prefix", defaultValue = "urn:anno4j") String prefix) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        return getAnnotationByPath(annoId, prefix, RDFFormat.TURTLE);
    }

    private String getAnnotationByPath(String annoId, String prefix, RDFFormat format) throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String annotationID = prefix + ":" + annoId;

        return findAnnotation(annotationID).getTriples(format);
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

    @ModelAttribute
    public void setLinkResponseHeader(HttpServletResponse response) {
        response.setHeader("Link", "http://www.w3.org/ns/ldp#Resource");
        response.setHeader("rel", "type");
    }



    // TODO retrieve anno with ?uri or ?ldpath
}
