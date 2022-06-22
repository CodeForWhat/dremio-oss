/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.physical.base;

import com.dremio.exec.catalog.ResolvedVersionContext;
import com.dremio.exec.record.BatchSchema;
import com.google.common.base.Preconditions;

/**
 * Save properties needed for creating views
 */
public class ViewOptions {
  private final ResolvedVersionContext version;
  private final BatchSchema batchSchema; // tracks the columns of the table to create from
  private final boolean isViewUpdate;

  private ViewOptions(ViewOptionsBuilder builder) {
    this.version = builder.version;
    this.batchSchema = builder.batchSchema;
    this.isViewUpdate = builder.isViewUpdate;
  }

  public ResolvedVersionContext getVersion(){
    return version;
  }

  public BatchSchema getBatchSchema() {return batchSchema;}

  public boolean isViewUpdate() { return isViewUpdate; }

  public static class ViewOptionsBuilder{
    private ResolvedVersionContext version;
    private BatchSchema batchSchema;
    private boolean isViewUpdate;

    public ViewOptionsBuilder() {
    }

    public ViewOptionsBuilder version(ResolvedVersionContext version){
      Preconditions.checkNotNull(version);
      this.version = version;
      return this;
    }

    public ViewOptionsBuilder batchSchema(BatchSchema schema){
      Preconditions.checkNotNull(schema);
      this.batchSchema = schema;
      return this;
    }

    public  ViewOptionsBuilder viewUpdate(boolean isViewUpdate) {
      this.isViewUpdate = isViewUpdate;
      return this;
    }

    public ViewOptions build() {
      ViewOptions viewOptions = new ViewOptions(this);
      return viewOptions;
    }
  }
}
