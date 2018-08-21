# aws-tag-automation
CRUD operations on aws resource tags using aws group resource API

### Operations:
- Validate – validate the given tag configuration.
- Update – Add/Update tag(s) for the given tag configuration.
- Delete – Remove (Un-tag) given tag configuration.
### Tag YAML configuration

```
# tag_config
# Specify set of resources and tags to be associated with the resources
# To specify all resources, use 'all' as the resource identifier
#########################################################################################
# tag config file structure
#########################################################################################
# region: 'aws region'
# operation: 'accepted values: UPDATE, DELETE, VALIDATE_KEY_ONLY, VALIDATE_KEY_VALUE'
# outputDir: 'out'
# tagParams:
# - resources:
#    type: 'any one value from ALL, SERVICE, ARN'
#    items:
#     - 's3'
#     - 'ec2'
#   tags:
#   - key: 'key-1'
#     value: 'value-1'
#   - key: 'key-2'
#     value: 'value-2'
# - resources:
#    type: 'SERVICE'
#    items:
#     - 's3'
#     - 'ec2'
#   tags:
#   - key: 'key-1'
#     value: 'value-1'
#   - key: 'key-2'
#     value: 'value-2'
############################################################################################
# Note: For 'VALIDATE_KEY_ONLY' operation just specify 'key' and no need for 'value'
#       To perform operation on all the supported resources mention 'all' as the resource.
#
#       For Resource type 'ALL' - items are ignored.
############################################################################################
```

### Tag YAML keys:
- **region** - The aws region we wish to run the script for.
- **operation** – update/validate/delete operations. 
  * **UPDATE** –  Tags will be added/modified as per the configuration. 
  * **VALIDATE_KEY_ONLY** – Check whether the given set of keys exists.
  * **VALIDATE_KEY_VALUE** –  Check whether both keys and values exist.
  * **DELETE** – Un-tag the given tags as per the configuration.
- **outputDir** -  Report will be generated for update and validate operations. This report will be persisted in the output directory mentioned in this tag. If this is not present, or the directory doesn’t exist the current directory in where the script is running will be considered as the output directory.
- **tagParams** – List of resources to be operated and tags configurations.
  * **type** - Each resource has a type parameter and list of items describing it.
  * Supported types are:
    * **ALL** – To specify all the supported resources in the region.
    * **SERVICE** – To specify services and resource types.
    * **ARN** – To specify an atomic resource (unique id for an AWS resource).
  * **items** – List of string adhering to the type of the resource.
    * In the case of type = ALL, the items are ignored as the script runs for all the supported resources in the region.
  * **tags** – List of key value pairs
    * key – Tag key
    * value – Tag value
    * In the case of **VALIDATE_KEY_VALUE** operation, the value field is ignored as we consider only keys of the tags.
    
### Build:
- Run the below command
```
mvn clean install
```
- On successful building, the “tagmanagement-1.0-SNAPSHOT-final.jar” is created in the target directory.

### Run:
- Make your required tag configuration in YAML file format.
- Run the “tagmanagement-1.0-SNAPSHOT-final.jar” and provide the YAML file path as command line argument.
```
java -jar tagmanagement-1.0-SNAPSHOT-final.jar <tag configuration yaml file path>
```
- You can see the terminal for intermediate status of the job. 
- Final output will be created in the configured output directory in xlsx format.


### License

[MIT](https://github.com/ramo/aws-tag-automation/blob/master/LICENSE)




