package domain.common;

import static foundation.Assert.*;

public class TextContent extends Content {
    // not null, not blank, max 1024 characters
    private String text;

    public TextContent(String text) {
        setText(text);
    }

    public void setText(String text) {
        hasMaxLength(text, 1025, "text");
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
