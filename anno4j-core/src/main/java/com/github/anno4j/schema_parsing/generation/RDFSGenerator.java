package com.github.anno4j.schema_parsing.generation;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.rdfs_parser.model.RDFSClazz;
import com.github.anno4j.rdfs_parser.model.RDFSProperty;
import com.github.anno4j.util.IdentifierUtil;
import com.squareup.javapoet.JavaFile;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Manu on 18/11/16.
 */
public class RDFSGenerator extends ModelGenerator {

    public RDFSGenerator(Anno4j anno4j, String packagePath, String namespace, String prefix) {
        super(anno4j, packagePath, namespace, prefix);
    }

    @Override
    public void generateModel() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        // TODO Check what classes are supposed to be created?
        List<RDFSClazz> clazzes = this.getAnno4j().createQueryService().execute(RDFSClazz.class);

        List<RDFSProperty> properties = this.getAnno4j().createQueryService().execute(RDFSProperty.class);

        NamespaceContainer nc = new NamespaceContainer(this.getNamespace(), this.getPrefix(), this.getPackagePath());
        JavaFile namespaceFile = nc.generate();

        HashMap<String, InterfaceContainer> map = new HashMap<>();

        for(RDFSClazz clazz : clazzes) {
            InterfaceContainer ic = new InterfaceContainer(clazz.getResourceAsString(), this.getPackagePath());

            map.put(IdentifierUtil.trimNamespace(clazz.getResourceAsString()), ic);
        }

        for(RDFSClazz clazz : clazzes) {
            for(RDFSClazz subClazz : clazz.getSubClazzes()) {
                map.get(IdentifierUtil.trimNamespace(subClazz.getResourceAsString())).addSuperClazz(clazz.getResourceAsString());
            }
        }

        for(RDFSProperty property : properties) {
            ResourceObject domain = (ResourceObject) property.getDomains().toArray()[0];
            map.get(IdentifierUtil.trimNamespace(domain.getResourceAsString())).addProperty(property);
        }

        List<JavaFile> files = new LinkedList<>();

        files.add(nc.generate());

        for(InterfaceContainer container : map.values()) {
            files.add(container.generateInterface());
            files.add(container.generateSupport());
        }

        System.out.println("test");
    }


}
