package com.github.anno4j.rdf_generation.tests;

import java.util.List;

import org.openrdf.annotations.Iri;


/**
 * A user playing a game.
 * Generated class for http://example.de/Player */
@Iri("http://example.de/Player")
public interface Player extends PlayerInterface, Rank {

    @Iri("http://example.de/rank")
    Integer getRank();
    
    @Iri("http://example.de/rank")
    void setRank(Integer score);
    
    @Iri("http://example.de/rank")
    Player getFirstPlace();
    
    @Iri("http://example.de/Pet")
    List<Pet> getPlayersPet(Player player);
}
