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

import org.apache.arrow.util.Preconditions;
import org.apache.calcite.sql.SqlOperatorTable;

import com.dremio.exec.catalog.SimpleCatalog;
import com.dremio.service.autocomplete.catalog.AutocompleteSchemaProvider;
import com.dremio.service.autocomplete.nessie.NessieElementReader;
import com.dremio.service.autocomplete.statements.grammar.Statement;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

public final class AutocompleteEngineContext {
  private final AutocompleteSchemaProvider autocompleteSchemaProvider;
  private final Supplier<NessieElementReader> nessieElementReaderSupplier;
  private final SqlOperatorTable operatorTable;
  private final SimpleCatalog<?> catalog;
  private final ImmutableList<Statement> currentStatementPath;

  public AutocompleteEngineContext(
    AutocompleteSchemaProvider autocompleteSchemaProvider,
    Supplier<NessieElementReader> nessieElementReaderSupplier,
    SqlOperatorTable operatorTable,
    SimpleCatalog<?> catalog,
    ImmutableList<Statement> currentStatementPath) {
    Preconditions.checkNotNull(autocompleteSchemaProvider);
    Preconditions.checkNotNull(nessieElementReaderSupplier);
    Preconditions.checkNotNull(operatorTable);
    Preconditions.checkNotNull(catalog);
    Preconditions.checkNotNull(currentStatementPath);
    this.autocompleteSchemaProvider = autocompleteSchemaProvider;
    this.nessieElementReaderSupplier = nessieElementReaderSupplier;
    this.operatorTable = operatorTable;
    this.catalog = catalog;
    this.currentStatementPath = currentStatementPath;
  }

  public ImmutableList<Statement> getCurrentStatementPath() {
    return currentStatementPath;
  }

  public AutocompleteSchemaProvider getAutocompleteSchemaProvider() {
    return autocompleteSchemaProvider;
  }

  public Supplier<NessieElementReader> getNessieElementReaderSupplier() {
    return nessieElementReaderSupplier;
  }

  public SqlOperatorTable getOperatorTable() {
    return operatorTable;
  }

  public SimpleCatalog<?> getCatalog() {
    return catalog;
  }
}