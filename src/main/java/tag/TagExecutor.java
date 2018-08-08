package tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import tag.model.TagConfig;
import tag.model.UpdateReportData;
import tag.model.ValidateReportData;
import tag.service.ReportService;
import tag.service.ReportServiceImpl;
import tag.service.TagService;
import tag.service.TagServiceImpl;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

public class TagExecutor {

    private TagService tagService = new TagServiceImpl();
    private ReportService reportService = new ReportServiceImpl();

    public void execute(URL conf) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        TagConfig tagConfig = mapper.readValue(conf, TagConfig.class);
        String outDir = tagConfig.getOutputDir();

        /**
         * Check if valid out directory, if not take current directory.
         */
        boolean outDirValid = false;
        try {
            outDirValid = outDir != null && !outDir.isEmpty() && new File(outDir).isDirectory();

        } catch(Exception ex) {}

        if (!outDirValid) {
            outDir = Paths.get(".").toAbsolutePath().normalize().toString();
        }

        switch (tagConfig.getOperation()) {
            case VALIDATE_KEY_VALUE:
                reportService.generateReport(outDir, new ValidateReportData(tagService.validateTags(tagConfig)));
                break;
            case VALIDATE_KEY_ONLY:
                reportService.generateReport(outDir, new ValidateReportData(tagService.validateTagKeys(tagConfig)));
                break;
            case UPDATE:
                reportService.generateReport(outDir, new UpdateReportData(tagService.update(tagConfig)));
                break;
            case DELETE:
                tagService.delete(tagConfig);
                break;
            default:
                System.out.println("Operation not supported: " + tagConfig.getOperation());
        }
    }

    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide tag configuration in yaml format!");
        } else {
            TagExecutor te = new TagExecutor();
            for (String arg : args) {
                System.out.println("Running for configuration : " + arg);
                System.out.println("===============================================================");
                te.execute(new File(arg).toURI().toURL());
                System.out.println("===============================================================");
                System.out.println("\n\n");
            }
        }
    }
}
