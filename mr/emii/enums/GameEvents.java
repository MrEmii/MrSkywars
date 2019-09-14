package mr.emii.enums;

public enum GameEvents {
    Reffil(15), DropLoot(15), End(15);

    private int timeLeft;

    GameEvents(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public GameEvents getNext() {
        return this.ordinal() < GameEvents.values().length - 1
                ? GameEvents.values()[this.ordinal() + 1]
                : null;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
