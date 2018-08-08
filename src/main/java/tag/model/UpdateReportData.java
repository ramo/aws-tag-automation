package tag.model;

import tag.utils.TagUtils;

import java.util.List;

public class UpdateReportData implements ReportData {

    private static final String REPORT_NAME = "tag-update-report";
    private static final String[] header = new String[] {
            "Resource Type",
            "Resource ARN",
            "Tag Key",
            "Tag Value",
            "Status",
            "Failure Reason"
    };

    private List<UpdatedRow> updatedRows;
    private Object[][] data;

    public UpdateReportData(List<UpdatedRow> updatedRows) {
        this.updatedRows = updatedRows;
        build();
    }

    private void build() {
        data = new Object[updatedRows.size()][header.length];
        for (int r = 0; r < updatedRows.size(); r++) {
            int c = 0;
            UpdatedRow ur = updatedRows.get(r);

            /**
             *             "Resource Type",
             *             "Resource ARN",
             *             "Tag Key",
             *             "Tag Value",
             *             "Status",
             *             "Failure Reason"
             */

            data[r][c++] = TagUtils.getServiceAndResourceType(ur.getArn());
            data[r][c++] = ur.getArn();
            data[r][c++] = ur.getTagKey();
            data[r][c++] = TagUtils.dashForEmpty(ur.getTagValue());
            data[r][c++] = ur.isSuccess() ? "Pass" : "Fail";
            data[r][c++] = TagUtils.dashForEmpty(ur.getFailureReason());
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
