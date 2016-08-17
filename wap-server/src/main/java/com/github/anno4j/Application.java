package com.github.anno4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan(basePackages = {"com.github.anno4j"})
public class Application extends SpringBootServletInitializer {

    private static Log logger = LogFactory.getLog(Application.class);

    @Value("${anno4j.wap.sparql.endpoint.select}")
    private String sparqlEndpointSelect;

    @Value("${anno4j.wap.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public Anno4j anno4j() throws RepositoryConfigException, RepositoryException {
        if(!sparqlEndpointSelect.equals("none") && !sparqlEndpointUpdate.equals("none")) {
            logger.info("Connecting to SPARQL endpoint " + sparqlEndpointSelect + " and " + sparqlEndpointUpdate);
            SPARQLRepository sparqlRepository = new SPARQLRepository(sparqlEndpointSelect, sparqlEndpointUpdate);
            return new Anno4j(sparqlRepository);
        } else {
            logger.info("No SPARQL endpoint configured. Creating In-Memory SPARQL endpoint");
            return new Anno4j();
        }
    }

    public static void main(String[] args) throws Throwable {
        SpringApplication app = new SpringApplication(Application.class);
        ConfigurableApplicationContext context = app.run(args);

        logger.info("Activated Spring Profiles: " + Arrays.toString(context.getEnvironment().getActiveProfiles()));
    }
}
