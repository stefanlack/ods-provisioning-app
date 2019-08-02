package org.opendevstack.provision.model.confluence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.opendevstack.provision.model.AtlassianPagedResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedConfluencSpaces implements AtlassianPagedResult<LeanConfluenceSpace> {

  @JsonProperty("start")
  public Integer start;

  @JsonProperty("size")
  public Integer size;

  @JsonProperty("limit")
  public Integer limit;

  @JsonProperty("results")
  List<LeanConfluenceSpace> data;

  public PagedConfluencSpaces() {}

  public PagedConfluencSpaces(List<LeanConfluenceSpace> data) {
    this.data = data;
  }

  @Override
  public List<LeanConfluenceSpace> getData() {
    return data;
  }

  @Override
  public void setData(List<LeanConfluenceSpace> data) {
    this.data = data;
  }

  @Override
  public Integer getStart() {
    return start;
  }

  @Override
  public void setStart(Integer start) {
    this.start = start;
  }

  @Override
  public Integer getSize() {
    return size;
  }

}
