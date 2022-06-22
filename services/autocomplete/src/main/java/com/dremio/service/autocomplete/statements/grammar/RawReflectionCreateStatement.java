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
package com.dremio.service.autocomplete.statements.grammar;

import static com.dremio.exec.planner.sql.parser.impl.ParserImplConstants.DISPLAY;

import org.apache.arrow.util.Preconditions;

import com.dremio.service.autocomplete.statements.visitors.StatementInputOutputVisitor;
import com.dremio.service.autocomplete.statements.visitors.StatementVisitor;
import com.dremio.service.autocomplete.tokens.DremioToken;
import com.dremio.service.autocomplete.tokens.TokenBuffer;
import com.google.common.collect.ImmutableList;

/**
 * ALTER TABLE CATALOG_PATH
 * ADD RAW REFLECTION name
 * USING
 * DISPLAY (field1, field2)
 * [ (STRIPED, CONSOLIDATED) PARTITION BY (field1, field2, ..) ]
 * [ DISTRIBUTE BY (field1, field2, ..) ]
 * [ LOCALSORT BY (field1, field2, ..) ]
 */
public final class RawReflectionCreateStatement extends Statement {
  private final CatalogPath catalogPath;
  private final String reflectionName;
  private final FieldList displayFields;
  private final FieldLists fieldLists;

  private RawReflectionCreateStatement(
    ImmutableList<DremioToken> tokens,
    CatalogPath catalogPath,
    String reflectionName,
    FieldList displayFields,
    FieldLists fieldLists) {
    super(tokens, ImmutableList.of());
    this.catalogPath = catalogPath;
    this.reflectionName = reflectionName;
    this.displayFields = displayFields;
    this.fieldLists = fieldLists;
  }

  public CatalogPath getCatalogPath() {
    return catalogPath;
  }

  public String getReflectionName() { return reflectionName; }

  public FieldList getDisplayFields() {
    return displayFields;
  }

  public FieldLists getFieldLists() {
    return fieldLists;
  }

  @Override
  public void accept(StatementVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public <I, O> O accept(StatementInputOutputVisitor<I, O> visitor, I input) {
    return visitor.visit(this, input);
  }

  static RawReflectionCreateStatement parse(
    TokenBuffer tokenBuffer,
    CatalogPath catalogPath,
    String reflectionName) {
    Preconditions.checkNotNull(tokenBuffer);
    Preconditions.checkNotNull(catalogPath);
    Preconditions.checkNotNull(reflectionName);

    return new Builder(tokenBuffer.toList(), catalogPath, reflectionName)
      .addDisplayFields(parseDisplayFields(tokenBuffer))
      .addFieldLists(FieldLists.parse(tokenBuffer))
      .build();
  }

  private static FieldList parseDisplayFields(TokenBuffer tokenBuffer) {
    if (tokenBuffer.isEmpty()) {
      return null;
    }

    tokenBuffer.readAndCheckKind(DISPLAY);
    if (tokenBuffer.isEmpty()) {
      return null;
    }

    return FieldList.parse(tokenBuffer);
  }

  private static final class Builder {
    private final ImmutableList<DremioToken> tokens;
    private final CatalogPath catalogPath;
    private final String tableName;
    private FieldList displayFields;
    private FieldLists fieldLists;

    public Builder (ImmutableList<DremioToken> tokens, CatalogPath catalogPath, String tableName) {
      this.tokens = tokens;
      this.catalogPath = catalogPath;
      this.tableName = tableName;
    }

    public Builder addDisplayFields(FieldList displayFields) {
      this.displayFields = displayFields;
      return this;
    }

    public Builder addFieldLists(FieldLists fieldLists) {
      this.fieldLists = fieldLists;
      return this;
    }

    public RawReflectionCreateStatement build() {
      return new RawReflectionCreateStatement(
        tokens,
        catalogPath,
        tableName,
        displayFields,
        fieldLists);
    }
  }
}
