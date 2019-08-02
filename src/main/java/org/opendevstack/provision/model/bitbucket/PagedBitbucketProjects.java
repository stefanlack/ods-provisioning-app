package org.opendevstack.provision.model.bitbucket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import org.opendevstack.provision.model.AtlassianPagedResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedBitbucketProjects implements AtlassianPagedResult<BitbucketProject> {

  @JsonProperty("start")
  public Integer start;

  @JsonProperty("size")
  public Integer size;

  @JsonProperty("limit")
  public Integer limit;

  @JsonProperty("values")
  List<BitbucketProject> data;


  public PagedBitbucketProjects() {
  }

  public PagedBitbucketProjects(Integer start, Integer size, Integer limit,
      List<BitbucketProject> data) {
    this.start = start;
    this.size = size;
    this.limit = limit;
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

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @Override
  public List<BitbucketProject> getData() {
    return data;
  }

  @Override
  public void setData(
      List<BitbucketProject> results) {
    this.data = results;
  }
}
