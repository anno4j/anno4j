package com.github.anno4j.rdf_generation.tests;

import java.util.List;

import org.openrdf.annotations.Iri;
import org.reflections.vfs.Vfs.File;

import com.github.anno4j.Anno4j;
import com.github.anno4j.annotations.Functional;
import com.github.anno4j.rdf_generation.generation.FileGenerator;

/**
 * A user playing a game.
 * Generated class for http://example.de/Player */
@Iri("http://example.de/Player")
public interface Player extends File, Rank {

    @Iri("http://example.de/rank")
    Integer getRank();
    
    @Iri("http://example.de/rank")
    void setRank(Integer score);
    
    @Iri("http://example.de/rank")
    String getFirstPlace();
    
    @Iri("http://example.de/Pet")
    List<Pet> getPlayersPet(Player player);
}
