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
package com.dremio.exec.planner.physical;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.RelNode;

import com.dremio.exec.catalog.DremioPrepareTable;
import com.dremio.exec.ops.OptimizerRulesContext;
import com.dremio.exec.planner.logical.Rel;
import com.dremio.exec.planner.logical.RelOptHelper;
import com.dremio.exec.planner.logical.TableModifyRel;
import com.dremio.exec.store.dfs.FileSystemPlugin;

/**
 * Generate physical plan for a FileSystemTableModifyPrule
 */
public class FileSystemTableModifyPrule extends TableModifyPruleBase {

  public FileSystemTableModifyPrule(OptimizerRulesContext context) {
    super(RelOptHelper.some(TableModifyRel.class, Rel.LOGICAL, RelOptHelper.any(RelNode.class)),
      "Prel.FileSystemTableModifyPrule", context);
  }

  @Override
  public boolean matches(RelOptRuleCall call) {
    return call.<TableModifyRel>rel(0).getCreateTableEntry().getPlugin() instanceof FileSystemPlugin;
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    onMatch(call,
      ((DremioPrepareTable) call.<TableModifyRel>rel(0).getTable()).getTable().getDataset());
  }
}
