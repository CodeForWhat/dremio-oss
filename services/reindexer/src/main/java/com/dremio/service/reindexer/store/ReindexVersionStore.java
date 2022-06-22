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

package com.dremio.service.reindexer.store;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.dremio.service.Service;
import com.dremio.service.reindexer.proto.ReindexVersionInfo;


/**
 * Store used to save and retrieve version info
 */
public interface ReindexVersionStore extends Service {

  /**
   * Creates/Saves version information
   * @param collectionName Collection name key
   * @param versionInfo Version Information
   */
  void save(String collectionName, ReindexVersionInfo versionInfo);

  /**
   * Updates ReindexVersionInfo with the modifier based on a condition
   * @param collectionName Collection name key
   * @param modifier The function that should modifies the ReindexVersionInfo
   * @param predicate condition to satisfy for update operation
   */
  void update(String collectionName, UnaryOperator<ReindexVersionInfo> modifier, Predicate<Integer> predicate) throws ReindexVersionStoreException;

  /**
   * Deletes version information for the given collection
   * @param collectionName Collection name key
   */
  void delete(String collectionName);

  /**
   * Get ReindexVersionInfo for a collection.
   *
   * @param collectionName name of the collection
   * @return {@link ReindexVersionInfo} reindex version info
   */
  ReindexVersionInfo get(String collectionName);
}
