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
package com.dremio.exec.store.iceberg;

import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.util.Text;

import com.dremio.exec.physical.config.TableFunctionConfig;
import com.dremio.exec.record.VectorAccessible;
import com.dremio.exec.store.OperationType;
import com.dremio.exec.store.RecordWriter;
import com.dremio.exec.store.dfs.AbstractTableFunction;
import com.dremio.exec.util.ColumnUtils;
import com.dremio.exec.util.VectorUtil;
import com.dremio.sabot.exec.context.OperatorContext;

/**
 * A table function that handles updating of the metadata for deleted data files.
 */
public class DeletedDataFilesMetadataTableFunction extends AbstractTableFunction {

  private IntVector outputOperationTypeVector;
  private VarCharVector inputFilePathVector;
  private VarCharVector outputPathVector;
  private BigIntVector inputRowCountVector;
  private BigIntVector outputRowCountVector;
  private long currentRowCount;

  private Text inputFilePath;
  private boolean doneWithRow;

  public DeletedDataFilesMetadataTableFunction(OperatorContext context, TableFunctionConfig functionConfig) {
    super(context, functionConfig);
  }

  @Override
  public VectorAccessible setup(VectorAccessible accessible) throws Exception {
    super.setup(accessible);

    outputOperationTypeVector = (IntVector) VectorUtil.getVectorFromSchemaPath(outgoing, RecordWriter.OPERATION_TYPE_COLUMN);

    inputFilePathVector = (VarCharVector) VectorUtil.getVectorFromSchemaPath(incoming, ColumnUtils.FILE_PATH_COLUMN_NAME);
    outputPathVector = (VarCharVector) VectorUtil.getVectorFromSchemaPath(outgoing, RecordWriter.PATH_COLUMN);

    inputRowCountVector = (BigIntVector) VectorUtil.getVectorFromSchemaPath(incoming, ColumnUtils.ROW_COUNT_COLUMN_NAME);
    outputRowCountVector = (BigIntVector) VectorUtil.getVectorFromSchemaPath(outgoing, RecordWriter.RECORDS_COLUMN);

    return outgoing;
  }

  @Override
  public void startBatch(int records) {
    outgoing.allocateNew();
  }

  @Override
  public void startRow(int row) throws Exception {
    inputFilePath = inputFilePathVector.getObject(row);
    doneWithRow = false;
    currentRowCount = inputRowCountVector.get(row);
  }

  @Override
  public int processRow(int startOutIndex, int maxRecords) throws Exception {
    if (doneWithRow) {
      return 0;
    } else {
      // OperationType is always OperationType.DELETE_DATAFILE.
      outputOperationTypeVector.setSafe(startOutIndex, OperationType.DELETE_DATAFILE.value);
      // inputFilePath is null for inserted rows in Merge query, skip
      if (inputFilePath != null) {
        outputPathVector.setSafe(startOutIndex, inputFilePath);
      }
      outputRowCountVector.setSafe(startOutIndex, currentRowCount);

      outgoing.setAllCount(startOutIndex + 1);
      doneWithRow = true;
      return 1;
    }
  }

  @Override
  public void closeRow() throws Exception {
  }
}
