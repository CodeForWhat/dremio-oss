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

public final class UpdateStatementCompletionTests extends AutocompleteEngineTests {
  @Test
  public void tests() {
    new GoldenFileTestBuilder<>(this::executeTestWithFolderContext)
      .add(
        "UPDATE",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE ^"))
      .add(
        "UPDATE + TABLE",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP ^"))
      .add(
        "UPDATE + TABLE + SET",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET ^"))
      .add(
        "UPDATE + TABLE + SET + PARTIAL ASSIGN",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = ^"))
      .add(
        "UPDATE + TABLE + SET + ASSIGN",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = 'Brandon' ^"))
      .add(
        "UPDATE + TABLE + SET + PARTIAL ASSIGN LIST",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = 'Brandon', ^"))
      .add(
        "UPDATE + TABLE + SET + ASSIGN LIST",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = 'Brandon', AGE = 27 ^"))
      .add(
        "UPDATE + TABLE + SET + ASSIGN LIST + WHERE",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = 'Brandon', AGE = 27 WHERE ^"))
      .add(
        "UPDATE + TABLE + SET + ASSIGN LIST + WHERE + BOOLEAN EXPRESSION",
        GoldenFileTestBuilder.MultiLineString.create("UPDATE EMP SET NAME = 'Brandon', AGE = 27 WHERE NAME != 'Brandon' ^"))
      .runTests();
  }
}
