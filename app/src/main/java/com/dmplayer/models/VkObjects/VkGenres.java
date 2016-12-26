package com.dmplayer.models.VkObjects;

public enum VkGenres {
    //TODO: hardcoded names, move to strings
    ROCK(1, "Rock"),
    POP(2, "Pop"),
    RAP_AND_HIP_HOP(3, "Rap & Hip-hop"),
    EASY_LISTENING(4, "Easy listening"),
    HOUSE_AND_DANCE(5, "House & Dance"),
    INSTRUMENTAL(6, "Instrumental"),
    METAL(7, "Metal"),
    ALTERNATIVE(21, "Alternative"),
    DUBSTEP(8, "Dubstep"),
    JAZZ_AND_BLUES(1001, "Jazz & Blues"),
    DRUM_AND_BASS(10, "Drum & Bass"),
    TRANCE(11, "Trance"),
    CHANSON(12, "Chanson"),
    ETHNIC(13, "Ethnic"),
    ACOUSTIC_AND_VOCAL(14, "Acoustic & Vocal"),
    REGGAE(15, "Reggae"),
    CLASSICAL(16, "Classical"),
    INDIE_POP(17, "Indie Pop"),
    SPEECH(19, "Speech"),
    ELECTROPOP_AND_DISCO(22, "Electropop & Disco"),
    OTHER(18, "Other");

    private int id;
    private String name;

    VkGenres(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
