package tag.service;

import tag.model.TagConfig;
import tag.model.UpdatedRow;
import tag.model.ValidatedRow;

import java.util.List;

public interface TagService {
    List<ValidatedRow> validateTagKeys(TagConfig tagConfig) throws Exception;
    List<ValidatedRow> validateTags(TagConfig tagConfig) throws Exception;
    List<UpdatedRow> update(TagConfig tagConfig) throws Exception;
    void delete(TagConfig tagConfig) throws Exception;
}
