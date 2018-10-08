package com.github.anno4j.rdf_generation.tests;

import org.openrdf.annotations.Iri;
import com.github.anno4j.annotations.Functional;

/**
 * A user playing a game.
 * Generated class for http://example.de/Player */
@Iri("http://example.de/Player")
public interface Player extends PlayerInterface {

    @Iri("http://example.de/rank")
    @Functional
    Integer getRank();
    
    @Iri("http://example.de/rank")
    void setRank(Integer score);
    
    @Iri("http://example.de/rank")
    Player getFirstPlace();
    
    @Iri("http://example.de/Pet")
    Pet getPlayersPet(Player player);
    
}