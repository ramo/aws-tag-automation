package tag.service;

import com.amazonaws.services.resourcegroupstaggingapi.model.ResourceTagMapping;

import java.util.List;

public interface ResourceService {
    List<String> getAllResources(String region) throws Exception;
    List<String> getResources(String region, List<String> services) throws Exception;

    List<ResourceTagMapping> getAllResourceTagMappings(String region) throws Exception;
    List<ResourceTagMapping> getResourceTagMappings(String region, List<String> services) throws Exception;
}
