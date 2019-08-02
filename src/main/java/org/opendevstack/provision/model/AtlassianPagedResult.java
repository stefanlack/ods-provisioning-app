package org.opendevstack.provision.model;

import java.util.List;

public interface AtlassianPagedResult<T> {

  Integer getStart();

  void setStart(Integer start);

  Integer getSize();

  List<T> getData();

  void setData(List<T> results);
}
