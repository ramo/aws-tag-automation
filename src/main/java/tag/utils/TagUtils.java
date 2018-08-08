package tag.utils;

import com.amazonaws.services.resourcegroupstaggingapi.model.Tag;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TagUtils {

    public static List<tag.model.Tag> convert(List<Tag> tagList) {
        List<tag.model.Tag> result = new ArrayList<>(tagList.size() + 1);
        for (Tag tag : tagList) {
            tag.model.Tag t = new tag.model.Tag();
            t.setKey(tag.getKey());
            t.setValue(tag.getValue());
            result.add(t);
        }
        return result;
    }

    public static Map<String, String> getTagHash(List<tag.model.Tag> tagList) {
        Map<String, String> hash = new HashMap<>();
        for (tag.model.Tag tag : tagList) {
            hash.put(tag.getKey(), tag.getValue());
        }
        return hash;
    }

    public static Map<String, String> getTagHashAWS(List<Tag> tagList) {
        Map<String, String> hash = new HashMap<>();
        for (Tag tag : tagList) {
            hash.put(tag.getKey(), tag.getValue());
        }
        return hash;
    }

    public static List<String> getKeys(List<tag.model.Tag> tags) {
        return tags.stream().map(t -> t.getKey()).collect(Collectors.toList());
    }

    public static String getServiceAndResourceType(String arn) {
        String splits[] = arn.split(":", 6);
        String service = splits[2];
        String resource = splits[5];
        String resourceType = "";
        if (resource.contains("/")) {
            resourceType = resource.split("/", 2)[0];
        } else if (resource.contains(":")) {
            resourceType = resource.split(":", 2)[0];
        }
        return service + " " + resourceType;
    }

    public static String dashForEmpty(String s) {
        return StringUtils.isEmpty(s) ? "-" : s;
    }

    public static void main(String args[]) {
//        String arn = "arn:aws:rds:us-west-2:dummy:cluster-pg:dummy-dummy";
//        String arn = "arn:aws:cloudformation:us-west-2:dummy:stack/dummy/dummy";
//        String arn = "arn:aws:ec2:us-west-2:dummy:instance/dummy";
        String arn = "arn:aws:s3:::test-test-test";
        System.out.println(TagUtils.getServiceAndResourceType(arn));
    }



    private TagUtils() {}
}
