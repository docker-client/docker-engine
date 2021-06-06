Generate/update api sources

```shell
docker run --rm -it \
  -v "${PWD}:/local" -w /local \
  openapitools/openapi-generator-cli generate \
  -c config-kotlin.yaml
```
