package tag.model;

public class ValidatedRow {
    private String arn;
    private String tagKey;
    private String tagValue;
    private String actualTagValue;
    private boolean tagMissing;
    private boolean valueMismatch;

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getActualTagValue() {
        return actualTagValue;
    }

    public void setActualTagValue(String actualTagValue) {
        this.actualTagValue = actualTagValue;
    }

    public boolean isTagMissing() {
        return tagMissing;
    }

    public void setTagMissing(boolean tagMissing) {
        this.tagMissing = tagMissing;
    }

    public boolean isValueMismatch() {
        return valueMismatch;
    }

    public void setValueMismatch(boolean valueMismatch) {
        this.valueMismatch = valueMismatch;
    }
}
