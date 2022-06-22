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
package com.dremio.services.nessie.grpc.client.v1api;

import static com.dremio.services.nessie.grpc.ProtoUtil.fromProto;
import static com.dremio.services.nessie.grpc.ProtoUtil.toProto;
import static com.dremio.services.nessie.grpc.client.GrpcExceptionMapper.handleNamespaceRetrieval;

import javax.annotation.Nullable;

import org.projectnessie.api.params.NamespaceParams;
import org.projectnessie.api.params.NamespaceParamsBuilder;
import org.projectnessie.client.api.GetNamespaceBuilder;
import org.projectnessie.error.NessieNamespaceNotFoundException;
import org.projectnessie.error.NessieReferenceNotFoundException;
import org.projectnessie.model.Namespace;

import com.dremio.services.nessie.grpc.api.NamespaceServiceGrpc.NamespaceServiceBlockingStub;

final class GrpcGetNamespace implements GetNamespaceBuilder {

  private final NamespaceServiceBlockingStub stub;
  private final NamespaceParamsBuilder builder = NamespaceParams.builder();

  GrpcGetNamespace(NamespaceServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  public GrpcGetNamespace namespace(Namespace namespace) {
    builder.namespace(namespace);
    return this;
  }

  @Override
  public GrpcGetNamespace refName(String refName) {
    builder.refName(refName);
    return this;
  }

  @Override
  public GetNamespaceBuilder hashOnRef(@Nullable String hashOnRef) {
    builder.hashOnRef(hashOnRef);
    return this;
  }

  @Override
  public Namespace get() throws NessieNamespaceNotFoundException, NessieReferenceNotFoundException {
    return handleNamespaceRetrieval(() -> fromProto(stub.getNamespace(toProto(builder.build()))));
  }
}
