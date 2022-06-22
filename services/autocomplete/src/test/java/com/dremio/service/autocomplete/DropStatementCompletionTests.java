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
package com.dremio.service.autocomplete;

import org.junit.Test;

import com.dremio.test.GoldenFileTestBuilder;

public final class DropStatementCompletionTests extends AutocompleteEngineTests {
  @Test
  public void tests() {
    new GoldenFileTestBuilder<>(this::executeTestWithRootContext)
      .add("Empty name",
        GoldenFileTestBuilder.MultiLineString.create("DROP TABLE ^"))
      .add("Works with folder name",
        GoldenFileTestBuilder.MultiLineString.create("DROP TABLE \"space\".^"))
      .add("Works with folder with spaces",
        GoldenFileTestBuilder.MultiLineString.create("DROP TABLE \"space with a space in the name\".^"))
      .add("Works with IF EXISTS",
        GoldenFileTestBuilder.MultiLineString.create("DROP TABLE IF EXISTS \"space\".^"))
      .add("Works with VIEW",
        GoldenFileTestBuilder.MultiLineString.create("DROP VIEW \"space\".^"))
      .add("Works with VDS",
        GoldenFileTestBuilder.MultiLineString.create("DROP VDS \"space\".^"))
      .runTests();
  }
}
