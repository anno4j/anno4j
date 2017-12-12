package com.github.anno4j.util.csv;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.openrdf.annotations.Iri;
import org.openrdf.repository.object.LangString;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link CSVParser}
 */
public class CSVParserTest {

    @Iri("http://example.org/King")
    public interface King extends ResourceObject {

        @Iri("http://example.org/label")
        Set<CharSequence> getLabel();

        @Iri("http://example.org/age")
        int getAge();

        @Iri("http://example.org/predecessor")
        Set<King> getPredecessors();

        @Iri("http://example.org/birthDate")
        Date getBirthDate();

        @Iri("http://example.org/birthDate")
        void setBirthDate(Date date);
    }

    @Test
    public void testParsing() throws Exception {
        Anno4j anno4j = new Anno4j(new SailRepository(new MemoryStore()), null, false);
        CSVParser parser = new CSVParser(anno4j);

        parser.registerPrefix("dbr", "http://dbpedia.org/page/");
        parser.registerPrefix("ex", "http://example.org/");

        ClassLoader classLoader = getClass().getClassLoader();
        URL csvUrl = classLoader.getResource("kings.rdf.csv");
        File csvFile = new File(csvUrl.getFile());
        CSVParser.CSVParserConfig config = new CSVParser.CSVParserConfig();
        config.setCharset(StandardCharsets.UTF_8);
        config.setTextDelimiter('\'');
        config.setFieldSeparator(';');

        parser.parseCSVFile(csvFile, config);

        King ludwig2 = anno4j.findByID(King.class, "http://dbpedia.org/page/Ludwig_II_of_Bavaria");
        King maximilian2 = anno4j.findByID(King.class, "http://dbpedia.org/page/Maximilian_II_of_Bavaria");

        assertEquals(53, ludwig2.getAge());
        // The date has to be after 1970 here. Else there is an unpredictable offset in the date:
        assertEquals("Sat Aug 26 00:00:00 CEST 2017", ludwig2.getBirthDate().toString());
        assertEquals(Sets.newHashSet(maximilian2), ludwig2.getPredecessors());
        assertTrue(ludwig2.getLabel().contains(new LangString("Ludwig II von Bayern", "de")));
        assertTrue(ludwig2.getLabel().contains(new LangString("Ludwig II of Bavaria", "en")));
        assertTrue(ludwig2.getLabel().contains("Ludwig II of Bavaria"));

        assertEquals(42, maximilian2.getAge());
    }
}