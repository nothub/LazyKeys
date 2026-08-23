package lol.hub.lazykeys;

public final class ToggleState {

    private boolean active;

    public boolean active() {
        return active;
    }

    public void toggle() {
        active = !active;
    }

}
