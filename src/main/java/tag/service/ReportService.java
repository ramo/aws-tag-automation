package tag.service;

import tag.model.ReportData;

public interface ReportService {
    void generateReport(String outDir, ReportData rd) throws Exception;
}
