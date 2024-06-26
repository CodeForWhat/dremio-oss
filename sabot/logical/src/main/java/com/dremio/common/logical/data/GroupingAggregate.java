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
package com.dremio.common.logical.data;

import com.dremio.common.expression.FieldReference;
import com.dremio.common.expression.LogicalExpression;
import com.dremio.common.logical.data.visitors.LogicalVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

@JsonTypeName("groupingaggregate")
public class GroupingAggregate extends SingleInputOperator {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GroupingAggregate.class);

  private final List<NamedExpression> keys;
  private final List<NamedExpression> exprs;

  public GroupingAggregate(
      @JsonProperty("keys") List<NamedExpression> keys,
      @JsonProperty("exprs") List<NamedExpression> exprs) {
    super();
    this.keys = keys;
    this.exprs = exprs;
  }

  @Override
  public <T, X, E extends Throwable> T accept(LogicalVisitor<T, X, E> logicalVisitor, X value)
      throws E {
    return logicalVisitor.visitGroupingAggregate(this, value);
  }

  @Override
  public Iterator<LogicalOperator> iterator() {
    return Iterators.singletonIterator(getInput());
  }

  public static Builder builder() {
    return new Builder();
  }

  public List<NamedExpression> getKeys() {
    return keys;
  }

  public List<NamedExpression> getExprs() {
    return exprs;
  }

  public static class Builder extends AbstractSingleBuilder<GroupingAggregate, Builder> {
    private List<NamedExpression> keys = Lists.newArrayList();
    private List<NamedExpression> exprs = Lists.newArrayList();

    public Builder addKey(FieldReference ref, LogicalExpression expr) {
      keys.add(new NamedExpression(expr, ref));
      return this;
    }

    public Builder addKey(NamedExpression expr) {
      keys.add(expr);
      return this;
    }

    public Builder addExpr(NamedExpression expr) {
      exprs.add(expr);
      return this;
    }

    public Builder addExpr(FieldReference ref, LogicalExpression expr) {
      exprs.add(new NamedExpression(expr, ref));
      return this;
    }

    @Override
    public GroupingAggregate internalBuild() {
      GroupingAggregate ga = new GroupingAggregate(keys, exprs);
      return ga;
    }
  }
}
