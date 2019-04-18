package entity.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMetaData {
    @JsonProperty("next_cursor")
    private String nextCursor;

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }
}
