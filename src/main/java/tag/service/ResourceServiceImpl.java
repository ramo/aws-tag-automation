package tag.service;

import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPI;
import com.amazonaws.services.resourcegroupstaggingapi.AWSResourceGroupsTaggingAPIClientBuilder;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesRequest;
import com.amazonaws.services.resourcegroupstaggingapi.model.GetResourcesResult;
import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceServiceImpl implements ResourceService {

    @Override
    public List<String> getAllResources(String region) throws Exception {
        return getARNs(region, null);
    }

    @Override
    public List<String> getResources(String region, List<String> services) throws Exception {
        return getARNs(region, services);
    }

    @Override
    public List<ResourceTagMapping> getAllResourceTagMappings(String region) throws Exception {
        return get(region, null);
    }

    @Override
    public List<ResourceTagMapping> getResourceTagMappings(String region, List<String> services) throws Exception {
        return get(region, services);
    }

    private List<String> getARNs(String region, List<String> services) throws Exception {
        return get(region, services).stream().map(rtm -> rtm.getResourceARN()).collect(Collectors.toList());
    }

    private List<ResourceTagMapping> get(String region, List<String> services) throws Exception {
        AWSResourceGroupsTaggingAPI tagAPI = null;
        try {
            AWSResourceGroupsTaggingAPIClientBuilder builder = AWSResourceGroupsTaggingAPIClientBuilder.standard();
            builder.setRegion(region);
            tagAPI = builder.build();

            GetResourcesRequest grr = new GetResourcesRequest();
            grr.setResourceTypeFilters(services);
            List<ResourceTagMapping> total = new LinkedList<>();
            do {
                GetResourcesResult grResult = tagAPI.getResources(grr);
                List<ResourceTagMapping> lst = grResult.getResourceTagMappingList();
                total.addAll(lst);
                grr.setPaginationToken(grResult.getPaginationToken());
            } while (!StringUtils.isEmpty(grr.getPaginationToken()));

            return total;
        } finally {
            if (tagAPI != null) tagAPI.shutdown();
        }
    }
}
