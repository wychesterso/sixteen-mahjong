package game.player.data;

public enum Seat {
    EAST("East", "東"),
    SOUTH("South", "南"),
    WEST("West", "西"),
    NORTH("North", "北"),
    ;

    private final String engSeat;
    private final String chiSeat;

    Seat(String engSeat, String chiSeat) {
        this.engSeat = engSeat;
        this.chiSeat = chiSeat;
    }

    public String getSeatNameEng() {
        return engSeat;
    }

    public String getSeatNameChi() {
        return chiSeat;
    }

    public String toString() {
        return getSeatNameEng() + " seat";
    }
}
