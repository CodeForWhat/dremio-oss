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
package com.dremio.common.scanner.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** describe a class that was annotated with one of the configured annotations */
public final class AnnotatedClassDescriptor {

  private final String className;
  private final List<AnnotationDescriptor> annotations;
  private final List<FieldDescriptor> fields;
  private final Map<String, AnnotationDescriptor> annotationMap;

  @JsonCreator
  public AnnotatedClassDescriptor(
      @JsonProperty("className") String className,
      @JsonProperty("annotations") List<AnnotationDescriptor> annotations,
      @JsonProperty("fields") List<FieldDescriptor> fields) {
    this.className = className;
    this.annotations = Collections.unmodifiableList(annotations);
    this.fields = Collections.unmodifiableList(fields);
    this.annotationMap = AnnotationDescriptor.buildAnnotationsMap(annotations);
  }

  /**
   * @return the fully qualified name of the annotated class
   */
  public String getClassName() {
    return className;
  }

  /**
   * @return the class level annotations
   */
  public List<AnnotationDescriptor> getAnnotations() {
    return annotations;
  }

  /**
   * @return the fields declared by the class
   */
  public List<FieldDescriptor> getFields() {
    return fields;
  }

  /**
   * @param clazz the annotation type
   * @return the corresponding annotation descriptor
   */
  public AnnotationDescriptor getAnnotation(Class<?> clazz) {
    return annotationMap.get(clazz.getName());
  }

  /**
   * @param clazz the annotation type
   * @return a bytecode based implementation of the annotation
   */
  public <T> T getAnnotationProxy(Class<T> clazz) {
    final AnnotationDescriptor annotationDescriptor = getAnnotation(clazz);
    if (annotationDescriptor == null) {
      return null;
    }
    return annotationDescriptor.getProxy(clazz);
  }

  @Override
  public String toString() {
    return "Function [className="
        + className
        + ", annotations="
        + annotations
        + ", fields="
        + fields
        + "]";
  }
}
