package tag.model;

import tag.utils.TagUtils;

import java.util.List;

public class ValidateReportData implements ReportData {

    private static final String REPORT_NAME = "tag-compliance-report";
    private static final String[] header = new String[] {
            "Resource Type",
            "Resource ARN",
            "Tag Key",
            "Tag Value",
            "Actual Tag Value",
            "Tag Missing",
            "Value Mismatch"
    };

    private List<ValidatedRow> validatedRows;
    private Object[][] data;

    public ValidateReportData(List<ValidatedRow> validatedRows) {
        this.validatedRows = validatedRows;
        build();
    }

    private void build() {
        data = new Object[validatedRows.size()][header.length];
        for (int r = 0; r < validatedRows.size(); r++) {
            int c = 0;
            ValidatedRow vr = validatedRows.get(r);

            /**
             *             "Resource Type",
             *             "Resource ARN",
             *             "Tag Key",
             *             "Tag Value",
             *             "Actual Tag Value",
             *             "Tag Missing",
             *             "Value Mismatch"
             */
            data[r][c++] = TagUtils.getServiceAndResourceType(vr.getArn());
            data[r][c++] = vr.getArn();
            data[r][c++] = vr.getTagKey();
            data[r][c++] = TagUtils.dashForEmpty(vr.getTagValue());
            data[r][c++] = TagUtils.dashForEmpty(vr.getActualTagValue());
            data[r][c++] = vr.isTagMissing() ? "Y" : "N";
            data[r][c++] = vr.isValueMismatch() ? "Y" : "N";
        }
    }

    @Override
    public String getReportName() {
        return REPORT_NAME;
    }

    @Override
    public String[] getHeader() {
        return header;
    }

    @Override
    public Object[][] getData() {
        return this.data;
    }
}
