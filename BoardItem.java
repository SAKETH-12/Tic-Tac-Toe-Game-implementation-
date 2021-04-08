package tictactoe;

import java.util.HashMap;
import java.util.Map;

public enum BoardItem {
    X("X"),
    EMPTY("_"),
    O("O");

    private static final Map<String, BoardItem> BY_LABEL = new HashMap<>();

    static {
        for (BoardItem e : values()) {
            BY_LABEL.put(e.item, e);
        }
    }

    private final String item;

    BoardItem(String x) {
        item = x;
    }

    @Override
    public String toString() {
        return item;
    }

    public static BoardItem toBoardItem(Character c) {
        return BY_LABEL.get(c.toString());
    }
}
