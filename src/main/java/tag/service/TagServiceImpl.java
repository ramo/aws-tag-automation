package tag.service;

import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIClientBuilder;
import com.amazonaws.services.resourcegroupstaggingapi.model.*;
import tag.model.*;
import tag.utils.TagUtils;

import java.util.*;

public class TagServiceImpl implements TagService {

    private ResourceService rss = new ResourceServiceImpl();

    @Override
    public List<ValidatedRow> validateTagKeys(TagConfig tagConfig) throws Exception {
        List<ValidatedRow> result = new LinkedList<>();
        for (TagParam tagParam : tagConfig.getTagParams()) {
            try {
                result.addAll(validate(tagConfig.getRegion(), tagParam, true));
            } catch (Exception ex) {
                System.err.println("Exception occurred while processing region: " + tagConfig.getRegion() + ", tagParam :" + tagParam);
                ex.printStackTrace();
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public List<ValidatedRow> validateTags(TagConfig tagConfig) throws Exception {
        List<ValidatedRow> result = new LinkedList<>();
        for (TagParam tagParam : tagConfig.getTagParams()) {
            try {
                result.addAll(validate(tagConfig.getRegion(), tagParam, false));
            } catch (Exception ex) {
                System.err.println("Exception occurred while processing region: " + tagConfig.getRegion() + ", tagParam :" + tagParam);
                ex.printStackTrace();
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public List<UpdatedRow> update(TagConfig tagConfig) throws Exception {
        List<UpdatedRow> result = new LinkedList<>();
        for (TagParam tagParam : tagConfig.getTagParams()) {
            try {
                result.addAll(update(tagConfig.getRegion(), tagParam));
            } catch (Exception ex) {
                System.err.println("Exception occurred while processing region: " + tagConfig.getRegion() + ", tagParam :" + tagParam);
                ex.printStackTrace();
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public void delete(TagConfig tagConfig) throws Exception {
        for (TagParam tagParam : tagConfig.getTagParams()) {
            delete(tagConfig.getRegion(), tagParam);
        }
    }

    private void delete(String region, TagParam tagParam) throws Exception {
        System.out.println("Delete tags for region: " + region + ", tagParam : " + tagParam);
        List<String> arns = getArns(region, tagParam);
        AWSResourceGroupsTaggingAPI tagAPI = null;
        try {
            AWSResourceGroupsTaggingAPIClientBuilder builder = AWSResourceGroupsTaggingAPIClientBuilder.standard();
            builder.setRegion(region);
            tagAPI = builder.build();

            int s = 0;
            int e = arns.size();
            int bs = 20;

            System.out.println("Total arns to be untagged : " + e);
            UntagResourcesRequest urr = new UntagResourcesRequest();
            urr.setTagKeys(TagUtils.getKeys(tagParam.getTags()));
            Map<String, FailureInfo> failure = new HashMap<>();
            do {
                int te = Math.min(s+bs, e);
                System.out.println("Un-tagging => [" + (s+1) + " - " + (te) + "]");

                urr.setResourceARNList(arns.subList(s, te));
                UntagResourcesResult urResult = tagAPI.untagResources(urr);
                failure.putAll(urResult.getFailedResourcesMap());
                s += bs;
            } while(s < e);

            System.out.println("Tag deletion completed.");
            System.out.println("Failure : " + failure);
        } finally {
            if (tagAPI != null) tagAPI.shutdown();
        }
    }

    private List<ValidatedRow> validate(String region, TagParam tagParam, boolean keyOnly) throws Exception {
        ResourceType type = tagParam.getResources().getType();
        List<ResourceTagMapping> total;

        if (type == ResourceType.ALL) {
            total = rss.getAllResourceTagMappings(region);
        } else {
            if (type != ResourceType.SERVICE)
                throw new UnsupportedOperationException(type + ", not supported!");
            total = rss.getResourceTagMappings(region, tagParam.getResources().getItems());
        }

        List<ValidatedRow> result = new LinkedList<>();
        System.out.println("Checking for resources :: " + tagParam.getResources());
        if (keyOnly)
            System.out.println("Validating keys :: " + TagUtils.getKeys(tagParam.getTags()));
        else
            System.out.println("Validating Tags :: " + tagParam.getTags());

        final Map<String, String> configTagHash = Collections.unmodifiableMap(TagUtils.getTagHash(tagParam.getTags()));
        for (ResourceTagMapping rtm : total) {
            Map<String, String> curHash = TagUtils.getTagHashAWS(rtm.getTags());
            String arn = rtm.getResourceARN();
            for (Iterator<String> itr = configTagHash.keySet().iterator(); itr.hasNext();) {
                String key = itr.next();
                String value = keyOnly ? null : configTagHash.get(key);
                boolean hasKey = curHash.containsKey(key);
                String curValue = curHash.get(key);
                boolean valueMismatch = value != null && !value.equals(curValue);
                if (!hasKey || valueMismatch) {
                    result.add(validatedRow(arn, key, value, curValue, !hasKey, valueMismatch));
                    System.out.println("Resources => " + rtm.getResourceARN());
                    System.out.println("Key : " + key + ", present => " + hasKey);
                    if (hasKey && value != null)
                        System.out.println("Expected value : " + value + ", present value: " + curValue);
                }
            }
        }

        return result;
    }

    private ValidatedRow validatedRow(String arn, String tagKey, String tagValue, String actualValue, boolean tagMissing, boolean valueMismatch) {
        ValidatedRow vr = new ValidatedRow();
        vr.setArn(arn);
        vr.setTagKey(tagKey);
        vr.setActualTagValue(actualValue);
        vr.setTagValue(tagValue);
        vr.setTagMissing(tagMissing);
        vr.setValueMismatch(valueMismatch);
        return vr;
    }

    private List<String> getArns(String region, TagParam tagParam) throws Exception {
        ResourceType type = tagParam.getResources().getType();
        List<String> arns = null;
        if (type == ResourceType.ARN) {
            arns = tagParam.getResources().getItems();
        } else if (type == ResourceType.ALL) {
            arns = rss.getAllResources(region);
        } else if (type == ResourceType.SERVICE) {
            arns = rss.getResources(region, tagParam.getResources().getItems());
        }

        if (arns == null || arns.isEmpty())
            throw new Exception("ARN values can't be get for the given input");


        /**
         * making sure that arns are unique for a set of tag updates.
         */
        return new ArrayList<>(new HashSet<>(arns));
    }

    private List<UpdatedRow> update(String region, TagParam tagParam) throws Exception {
        System.out.println("Tagging for region: " + region + " with tagParam: " + tagParam);
        AWSResourceGroupsTaggingAPI tagAPI = null;
        List<String> arns = getArns(region, tagParam);
        try {
            List<UpdatedRow> result = new LinkedList<>();
            AWSResourceGroupsTaggingAPIClientBuilder builder = AWSResourceGroupsTaggingAPIClientBuilder.standard();
            builder.setRegion(region);
            tagAPI = builder.build();
            TagResourcesRequest trr = new TagResourcesRequest();
            trr.setTags(TagUtils.getTagHash(tagParam.getTags()));
            Map<String, FailureInfo> failure = new HashMap<>();

            int s = 0;
            int e = arns.size();
            int bs = 20;

            System.out.println("Total ARNs to be tagged: " + e);

            do {
                int te = Math.min(s+bs, e);
                System.out.println("Tagging => [" + (s+1) + " - " + (te) + "]");
                try {
                    trr.setResourceARNList(arns.subList(s, te));
                    TagResourcesResult trResult = tagAPI.tagResources(trr);
                    Map<String, FailureInfo> curFailure = trResult.getFailedResourcesMap();
                    failure.putAll(curFailure);

                    // storing intermediate results for the reporting purpose
                    for (String arn : trr.getResourceARNList()) {
                        String failureReason = null;
                        boolean success = true;
                        if (curFailure.containsKey(arn)) {
                            success = false;
                            failureReason = curFailure.get(arn).toString();
                        }
                        result.addAll(updatedRows(arn, trr.getTags(), success, failureReason));
                    }
                } catch (Exception ex) {
                    System.err.println("Exception occurred while tagging for : " + trr.getResourceARNList() + ", tags = " + trr.getTags());
                    ex.printStackTrace();
                }
                s += bs;
            } while(s < e);

            System.out.println("Tagging update completed.");
            System.out.println("Failure => " + failure);

            return result;

        } finally {
            if (tagAPI != null) tagAPI.shutdown();
        }
    }

    private List<UpdatedRow> updatedRows(String arn, Map<String, String> tags, boolean success, String failureReason) {
        List<UpdatedRow> urList = new ArrayList<>(tags.size());
        for (Iterator<String> itr = tags.keySet().iterator(); itr.hasNext();) {
            UpdatedRow ur = new UpdatedRow();
            ur.setArn(arn);
            ur.setFailureReason(failureReason);
            ur.setSuccess(success);
            String key = itr.next();
            ur.setTagKey(key);
            ur.setTagValue(tags.get(key));
            urList.add(ur);
        }
        return urList;
    }
}
