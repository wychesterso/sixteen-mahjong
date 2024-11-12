package game.board.tile;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of constant Mahjong tiles used in the game.
 */
public enum Tile {
    TUNG_1(TileType.TUNG, 1, "🀙"),
    TUNG_2(TileType.TUNG, 2, "🀚"),
    TUNG_3(TileType.TUNG, 3, "🀛"),
    TUNG_4(TileType.TUNG, 4, "🀜"),
    TUNG_5(TileType.TUNG, 5, "🀝"),
    TUNG_6(TileType.TUNG, 6, "🀞"),
    TUNG_7(TileType.TUNG, 7, "🀟"),
    TUNG_8(TileType.TUNG, 8, "🀠"),
    TUNG_9(TileType.TUNG, 9, "🀡"),

    SOK_1(TileType.SOK, 1, "🀐"),
    SOK_2(TileType.SOK, 2, "🀑"),
    SOK_3(TileType.SOK, 3, "🀒"),
    SOK_4(TileType.SOK, 4, "🀓"),
    SOK_5(TileType.SOK, 5, "🀔"),
    SOK_6(TileType.SOK, 6, "🀕"),
    SOK_7(TileType.SOK, 7, "🀖"),
    SOK_8(TileType.SOK, 8, "🀗"),
    SOK_9(TileType.SOK, 9, "🀘"),

    MAAN_1(TileType.MAAN, 1, "🀇"),
    MAAN_2(TileType.MAAN, 2, "🀈"),
    MAAN_3(TileType.MAAN, 3, "🀉"),
    MAAN_4(TileType.MAAN, 4, "🀊"),
    MAAN_5(TileType.MAAN, 5, "🀋"),
    MAAN_6(TileType.MAAN, 6, "🀌"),
    MAAN_7(TileType.MAAN, 7, "🀍"),
    MAAN_8(TileType.MAAN, 8, "🀎"),
    MAAN_9(TileType.MAAN, 9, "🀏"),

    WIND_EAST(TileType.WORD_WIND, 1, "🀀"),
    WIND_SOUTH(TileType.WORD_WIND, 2, "🀁"),
    WIND_WEST(TileType.WORD_WIND, 3, "🀂"),
    WIND_NORTH(TileType.WORD_WIND, 4, "🀃"),
    WORD_ZHONG(TileType.WORD_DRAGON, 5, "\uD83C\uDC04"),
    WORD_FAT(TileType.WORD_DRAGON, 6, "🀅"),
    WORD_BAT(TileType.WORD_DRAGON, 7, "🀆"),

    FLOWER_SPRING(TileType.FLOWER_SEASON, 1, "🀦"),
    FLOWER_SUMMER(TileType.FLOWER_SEASON, 2, "🀧"),
    FLOWER_AUTUMN(TileType.FLOWER_SEASON, 3, "🀨"),
    FLOWER_WINTER(TileType.FLOWER_SEASON, 4, "🀩"),
    FLOWER_MUI(TileType.FLOWER_PLANT, 5, "🀢"),
    FLOWER_LAN(TileType.FLOWER_PLANT, 6, "🀣"),
    FLOWER_KUK(TileType.FLOWER_PLANT, 7, "🀥"),
    FLOWER_CHUK(TileType.FLOWER_PLANT, 8, "🀤");

    private final TileType tileType;
    private final int tileNum;
    private final String engTile;
    private final String chiTile;
    private final String tileString;
    private final List<String> tileNames = new ArrayList<>();

    /**
     * Creates a new tile.
     * @param tileType the type of the tile.
     * @param tileNum the number of the tile within its own type.
     * @param tileString the tile's unicode character.
     */
    Tile(TileType tileType, int tileNum, String tileString) {
        this.tileType = tileType;
        this.tileNum = tileNum;
        this.tileString = tileString;
        switch (tileType) {
            case TUNG, SOK, MAAN -> {
                this.engTile = tileNum + " " + tileType.getTypeNameEng();
                this.chiTile = intToChi(tileNum) + tileType.getTypeNameChi();
                this.tileNames.add(intToEng(tileNum) + " " + tileType.getTypeNameEng());
                this.tileNames.add(tileNum + " " + tileType.getTypeNameEngFancy());
                this.tileNames.add(intToEng(tileNum) + " " + tileType.getTypeNameEngFancy());
            }
            case WORD_WIND, WORD_DRAGON -> {
                switch (tileNum) {
                    case 1 -> {
                        this.engTile = "East";
                        this.chiTile = "東";
                    }
                    case 2 -> {
                        this.engTile = "South";
                        this.chiTile = "南";
                    }
                    case 3 -> {
                        this.engTile = "West";
                        this.chiTile = "西";
                    }
                    case 4 -> {
                        this.engTile = "North";
                        this.chiTile = "北";
                    }
                    case 5 -> {
                        this.engTile = "Red Dragon";
                        this.chiTile = "中";
                        this.tileNames.add("Zhong");
                    }
                    case 6 -> {
                        this.engTile = "Green Dragon";
                        this.chiTile = "發";
                        this.tileNames.add("Fat");
                    }
                    case 7 -> {
                        this.engTile = "White Dragon";
                        this.chiTile = "白";
                        this.tileNames.add("Bat");
                        this.tileNames.add("Blank");
                    }
                    default -> {
                        throw new IllegalArgumentException();
                    }
                }
            }
            case FLOWER_SEASON, FLOWER_PLANT -> {
                switch (tileNum) {
                    case 1 -> {
                        this.engTile = "Spring";
                        this.chiTile = "春";
                    }
                    case 2 -> {
                        this.engTile = "Summer";
                        this.chiTile = "夏";
                    }
                    case 3 -> {
                        this.engTile = "Autumn";
                        this.chiTile = "秋";
                    }
                    case 4 -> {
                        this.engTile = "Winter";
                        this.chiTile = "冬";
                    }
                    case 5 -> {
                        this.engTile = "Plum Blossom";
                        this.chiTile = "梅";
                    }
                    case 6 -> {
                        this.engTile = "Orchid";
                        this.chiTile = "蘭";
                    }
                    case 7 -> {
                        this.engTile = "Chrysanthemum";
                        this.chiTile = "菊";
                    }
                    case 8 -> {
                        this.engTile = "Bamboo";
                        this.chiTile = "竹";
                    }
                    default -> {
                        throw new IllegalArgumentException();
                    }
                }
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }

        this.tileNames.add(this.engTile);
        this.tileNames.add(this.chiTile);
    }

    /**
     * Converts an arabic numeral single digit integer to English for display.
     * @param num the integer to be converted.
     * @return the English representation of the integer.
     * @throws IllegalArgumentException if num is not in range 1-9.
     */
    private static String intToEng(int num) {
        String output;
        switch (num) {
            case 1 -> output = "One";
            case 2 -> output = "Two";
            case 3 -> output = "Three";
            case 4 -> output = "Four";
            case 5 -> output = "Five";
            case 6 -> output = "Six";
            case 7 -> output = "Seven";
            case 8 -> output = "Eight";
            case 9 -> output = "Nine";
            default -> throw new IllegalArgumentException();
        }
        return output;
    }

    /**
     * Converts an arabic numeral single digit integer to Chinese for display.
     * @param num the integer to be converted.
     * @return the Chinese representation of the integer.
     * @throws IllegalArgumentException if num is not in range 1-9.
     */
    private static String intToChi(int num) {
        String output;
        switch (num) {
            case 1 -> output = "一";
            case 2 -> output = "二";
            case 3 -> output = "三";
            case 4 -> output = "四";
            case 5 -> output = "五";
            case 6 -> output = "六";
            case 7 -> output = "七";
            case 8 -> output = "八";
            case 9 -> output = "九";
            default -> throw new IllegalArgumentException();
        }
        return output;
    }

    /**
     * Retrieves the tile's type.
     * @return the type of the tile.
     */
    public TileType getTileType() {
        return tileType;
    }

    /**
     * Retrieves the tile's number within its own type.
     * @return the number of the tile.
     */
    public int getTileNum() {
        return tileNum;
    }

    /**
     * Retrieves the English name of the tile.
     * @return the English name of the tile.
     */
    public String getTileEng() {
        return engTile;
    }

    /**
     * Retrieves the Chinese name of the tile.
     * @return the Chinese name of the tile.
     */
    public String getTileChi() {
        return chiTile;
    }

    /**
     * Retrieves a list of valid names for this tile.
     * @return the list of names.
     */
    public List<String> getTileNames() {
        return new ArrayList<>(this.tileNames);
    }

    /**
     * Retrieves the unicode character of the tile.
     * @return the unicode character of the tile.
     */
    @Override
    public String toString() {
        return tileString;
    }
}
