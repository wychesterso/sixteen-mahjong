package game.board.tile;

/**
 * A collection of the different constant tile types.
 */
public enum TileType {
    TUNG("Tung", "Circle", "筒"),
    SOK("Sok", "Bamboo", "索"),
    MAAN("Maan", "Million", "萬"),
    WORD_WIND("Word (Wind)", "Wind", "字(風)"),
    WORD_DRAGON("Word (Dragon)", "Dragon", "字(元)"),
    FLOWER_SEASON("Flower (Season)", "Season", "花(季)"),
    FLOWER_PLANT("Flower (Plant)", "Flower", "花(君)"),
    ;

    private final String engType;
    private final String engFancyType;
    private final String chiType;

    /**
     * Creates a tile type.
     * @param engType the English name of the type.
     * @param engFancyType the official English name of the type.
     * @param chiType the Chinese name of the type.
     */
    TileType(String engType, String engFancyType, String chiType) {
        this.engType = engType;
        this.engFancyType = engFancyType;
        this.chiType = chiType;
    }

    /**
     * Retrieves the English type name.
     * @return the English type name.
     */
    public String getTypeNameEng() {
        return engType;
    }

    /**
     * Retrieves the official English type name.
     * @return the official English type name.
     */
    public String getTypeNameEngFancy() {
        return engFancyType;
    }

    /**
     * Retrieves the Chinese type name.
     * @return the Chinese type name.
     */
    public String getTypeNameChi() {
        return chiType;
    }
}
