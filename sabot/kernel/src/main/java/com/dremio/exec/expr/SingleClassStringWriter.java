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
package com.dremio.exec.expr;

import com.google.common.base.Preconditions;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

public class SingleClassStringWriter extends CodeWriter {
  static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SingleClassStringWriter.class);

  private boolean used;
  private StringWriter writer = new StringWriter();

  @Override
  public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Writer openSource(JPackage pkg, String fileName) throws IOException {
    Preconditions.checkArgument(
        !used, "The SingleClassStringWriter can only output once src file.");
    used = true;
    return writer;
  }

  @Override
  public void close() throws IOException {}

  public StringBuffer getCode() {
    return writer.getBuffer();
  }
}
