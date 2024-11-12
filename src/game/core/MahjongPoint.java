package game.core;

public enum MahjongPoint {
    ZHONG("莊", "Zhong", 1),
    SELF_DRAW("自摸", "Self-Draw", 1),
    FA_SHEUNG_SELF_DRAW("花上自摸", "Self-Draw after flower draw", 1),
    KONG_SHEUNG_SELF_DRAW("摃上自摸", "Self-Draw after Kong", 1),
    CHEUNG_KONG_SIK_WU("搶摃吃糊", "Win by stealing from opponent Kong", 1),
    KONG_SHEUNG_KONG_SELF_DRAW("摃上摃自摸", "Self-Draw after two consecutive Kongs", 30),
    CHEUNG_KONG_SHEUNG_KONG_SELF_DRAW("搶摃上摃自摸", "Self-Draw after Kong after stealing from Kong", 30),
    MULTIPLE_WINNERS("雙響", "Multiple winners", 10),
    MO_FA("無花", "No flowers", 1),
    ZENG_FA("正花", "Flower corresponding to seat", 2),
    LAN_FA("爛花", "Flower not corresponding to seat", 1),
    YAT_TOI_FA("一台花", "Four flower tiles of the same type", 10),
    LEUNG_TOI_FA("兩台花", "All flower tiles", 30),
    MO_ZI("無字", "No word tiles", 1),
    MO_ZI_FA("無字花", "No word or flower tiles", 5),
    EAST("東風", "East Wind", 1),
    SOUTH("南風", "South Wind", 1),
    WEST("西風", "West Wind", 1),
    NORTH("北風", "North Wind", 1),
    RED_DRAGON("紅中", "Red Dragon", 2),
    GREEN_DRAGON("發財", "Green Dragon", 2),
    WHITE_DRAGON("白板", "White Dragon", 2),
    CHICKEN_HAND("雞糊", "Chicken Hand", 10),
    DUI_PONG("對碰", "Call with two pairs", 1),
    GA_DUK("假獨", "Call on one tile (sort of)", 1),
    DUK_DUK("獨獨", "Call on one tile", 2),
    TSEUNG_AN("將眼", "Eye is 2/5/8", 1),
    LOU_SIU("老少", "123 and 789, or 111 and 999, same type", 2),
    BRIGHT_KONG("明摃", "Bright Kong", 1),
    DARK_KONG("暗摃", "Dark Kong", 2),
    PING_WU("平糊", "All groups are Sheungs", 3),
    MO_ZI_FA_PING_WU("大平糊", "No word or flower tiles, all groups are Sheungs", 10),
    MUN_TSING("門前清", "No revealed groups", 3),
    MUN_TSING_SELF_DRAW("門清自摸", "Self-draw and no revealed groups", 5),
    KUT_YAT_MUN("缺一門", "Only contains two of the following: Tung, Sok, Maan",5),
    MM_MUN_CHAI("五門齊", "Contains all of Tung, Sok, Maan, Wind, and Dragon", 5),
    TSAT_MUN_CHAI("七門齊", "Contains all of Tung, Sok, Maan, Wind, Dragon, and both flower types", 10),
    YEE_UM_HAK("二暗刻", "Two groups of 3+ identical tiles, unrevealed", 3),
    SAM_UM_HAK("三暗刻", "Three groups of 3+ identical tiles, unrevealed", 10),
    SEI_UM_HAK("四暗刻", "Four groups of 3+ identical tiles, unrevealed", 30),
    MM_UM_HAK("五暗刻", "Five groups of 3+ identical tiles, unrevealed", 80),
    YAT_PUN_KO("一般高", "Two identical Sheungs", 3),
    SAM_PUN_KO("三般高", "Three identical Sheungs", 15),
    SEI_PUN_KO("四般高", "Four identical Sheungs", 30),
    YEE_SHEUNG_FUNG("二相逢", "Two Sheungs of same numbers but different types", 2),
    SAM_SHEUNG_FUNG("三相逢", "Three Sheungs of same numbers but different types", 10),
    SEI_TONG_SHUN("四同順", "Four Sheungs of same numbers", 20),
    MM_TONG_SHUN("五同順", "Five Sheungs of same numbers", 40),
    YEE_HING_DAI("二兄弟", "Two Pongs/Kongs of same number", 3),
    SIU_SAM_HING_DAI("小三兄弟", "Two Pongs/Kongs plus a pair of same number", 10),
    DAI_SAM_HING_DAI("大三兄弟", "Three Pongs/Kongs of same number", 15),
    SIU_SAM_TSZ_MUI("小三姊妹", "Two Pongs/Kongs of same type and adjacent numbers, plus a pair adjacent to it", 8),
    DAI_SAM_TSZ_MUI("大三姊妹", "Three Pongs/Kongs of same type and adjacent numbers", 15),
    SEI_KWAI_YAT("四歸一", "Four identical tiles used in two different groups", 5),
    SEI_KWAI_YEE("四歸二", "Four identical tiles used in three different groups", 10),
    SEI_KWAI_SEI("四歸四", "Four identical tiles used in four different groups", 20),
    MING_LUNG("明龍", "123, 456, 789 groups of the same type", 10),
    UM_LUNG("暗龍", "123, 456, 789 groups of the same type, none revealed", 20),
    MING_ZHAP_LUNG("明雜龍", "123, 456, 789 groups of three different types", 8),
    UM_ZHAP_LUNG("暗雜龍", "123, 456, 789 groups of three different types, none revealed", 15),
    DUI_DUI_WU("對對糊", "All groups are Pongs/Kongs", 30),
    WUN_YAT_SIK("混一色", "Only contains word tiles and one of the following: Tung, Sok, Maan", 30),
    TSING_YAT_SIK("清一色", "Only contains one of the following: Tung, Sok, Maan", 80),
    DUEN_YIU("斷么", "No 1, 9, or word tiles", 5),
    TSUEN_DAI_WUN_YIU("全帶混么", "Every group/pair has 1, 9, or word tiles", 10),
    TSUEN_DAI_YIU("全帶么", "Every group/pair has 1 or 9, no word tiles", 15),
    WUN_YIU("混么", "All tiles are 1, 9, or word tiles", 30),
    TSING_YIU("清么", "All tiles are 1 or 9", 80),
    TSUEN_KAU_YAN("全求人", "All groups are revealed, and won off another player", 15),
    BOON_KAU_YAN("半求人", "All groups are revealed, and won off self-draw", 8),
    SAP_TSEK_LOI("十只內", "Only ten or less tiles to draw", 10),
    TSAT_TSEK_LOI("七只內", "Only seven or less tiles to draw", 20),
    HOI_DAI_LAO_YUET("海底撈月", "Win on last tile draw", 20),
    SIU_SAM_YUEN("小三元", "Contains two Pongs/Kongs and one pair of the three Dragon tiles", 20),
    DAI_SAM_YUEN("大三元", "Contains three Pongs/Kongs of the three Dragon tiles", 40),
    SIU_SAM_FUNG("小三風", "Contains two Pongs/Kongs and one pair of the four Wind tiles", 15),
    DAI_SAM_FUNG("大三風", "Contains three Pongs/Kongs of the four Wind tiles", 30),
    SIU_SEI_HEI("小四喜", "Contains three Pongs/Kongs and one pair of the four Wind tiles", 60),
    DAI_SEI_HEI("大四喜", "Contains four Pongs/Kongs of the four Wind tiles", 80),
    SAP_SAM_YIU("十三么", "The thirteen orphan tiles with one duplicate tile, plus any unrevealed group", 80),
    SAP_LUK_BAT_DAP("十六不搭", "Sixteen tiles that cannot form any group with one another, with one duplicate tile", 40),
    LIK_KU_LIK_KU("嚦咕嚦咕", "Seven pairs and one Pong, all unrevealed", 40),
    KAN_KAN_WU("坎坎糊", "Self-draw, no revealed groups, and all Pongs/Kongs", 100),
    TIN_WU("天糊", "Zhong win on first draw", 100),
    DEI_WU("地糊", "Win on first discard", 80),
    YAN_WU("人糊", "Win within four discards", 80),
    ;

    private final String pointNameChi;
    private final String description;
    private int pointScore;

    MahjongPoint(String pointNameChi, String description, int pointScore) {
        this.pointNameChi = pointNameChi;
        this.description = description;
        this.pointScore = pointScore;
    }

    public String getPointNameChi() {
        return pointNameChi;
    }

    public int getPointScore() {
        return pointScore;
    }

    public void setPointScore(int score) {
        this.pointScore = score;
    }

    public void addPointScore(int amount) {
        this.pointScore += amount;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return pointNameChi + ", " + pointScore;
    }
}
