package tag.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class TagConfig implements Serializable {
    private String region;
    private TagOperation operation;
    private String outputDir;
    private List<TagParam> tagParams;

    public TagConfig() {
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public TagOperation getOperation() {
        return operation;
    }

    public void setOperation(TagOperation operation) {
        this.operation = operation;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public List<TagParam> getTagParams() {
        return tagParams;
    }

    public void setTagParams(List<TagParam> tagParams) {
        this.tagParams = tagParams;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
