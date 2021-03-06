/*
 * Copyright 2018, OpenCensus Authors
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

package io.opencensus.exporter.trace.stackdriver;

import com.google.auth.Credentials;
import com.google.auto.value.AutoValue;
import com.google.cloud.ServiceOptions;
import com.google.cloud.trace.v2.stub.TraceServiceStub;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.opencensus.trace.AttributeValue;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Configurations for {@link StackdriverTraceExporter}.
 *
 * @since 0.12
 */
@AutoValue
@Immutable
public abstract class StackdriverTraceConfiguration {

  private static final String DEFAULT_PROJECT_ID =
      Strings.nullToEmpty(ServiceOptions.getDefaultProjectId());

  StackdriverTraceConfiguration() {}

  /**
   * Returns the {@link Credentials}.
   *
   * @return the {@code Credentials}.
   * @since 0.12
   */
  @Nullable
  public abstract Credentials getCredentials();

  /**
   * Returns the cloud project id.
   *
   * @return the cloud project id.
   * @since 0.12
   */
  public abstract String getProjectId();

  /**
   * Returns a TraceServiceStub instance used to make RPC calls.
   *
   * @return the trace service stub.
   * @since 0.16
   */
  @Nullable
  public abstract TraceServiceStub getTraceServiceStub();

  /**
   * Returns a map of attributes that is added to all the exported spans.
   *
   * @return the map of attributes that is added to all the exported spans.
   * @since 0.19
   */
  public abstract Map<String, AttributeValue> getFixedAttributes();

  /**
   * Returns a new {@link Builder}.
   *
   * @return a {@code Builder}.
   * @since 0.12
   */
  public static Builder builder() {
    return new AutoValue_StackdriverTraceConfiguration.Builder()
        .setProjectId(DEFAULT_PROJECT_ID)
        .setFixedAttributes(Collections.<String, AttributeValue>emptyMap());
  }

  /**
   * Builder for {@link StackdriverTraceConfiguration}.
   *
   * @since 0.12
   */
  @AutoValue.Builder
  public abstract static class Builder {

    Builder() {}

    /**
     * Sets the {@link Credentials} used to authenticate API calls.
     *
     * @param credentials the {@code Credentials}.
     * @return this.
     * @since 0.12
     */
    public abstract Builder setCredentials(Credentials credentials);

    /**
     * Sets the cloud project id.
     *
     * @param projectId the cloud project id.
     * @return this.
     * @since 0.12
     */
    public abstract Builder setProjectId(String projectId);

    /**
     * Sets the trace service stub used to send gRPC calls.
     *
     * @param traceServiceStub the {@code TraceServiceStub}.
     * @return this.
     * @since 0.16
     */
    public abstract Builder setTraceServiceStub(TraceServiceStub traceServiceStub);

    /**
     * Sets the map of attributes that is added to all the exported spans.
     *
     * @param fixedAttributes the map of attributes that is added to all the exported spans.
     * @return this.
     * @since 0.16
     */
    public abstract Builder setFixedAttributes(Map<String, AttributeValue> fixedAttributes);

    abstract String getProjectId();

    abstract Map<String, AttributeValue> getFixedAttributes();

    abstract StackdriverTraceConfiguration autoBuild();

    /**
     * Builds a {@link StackdriverTraceConfiguration}.
     *
     * @return a {@code StackdriverTraceConfiguration}.
     * @since 0.12
     */
    public StackdriverTraceConfiguration build() {
      // Make a defensive copy of fixed attributes.
      setFixedAttributes(
          Collections.unmodifiableMap(
              new LinkedHashMap<String, AttributeValue>(getFixedAttributes())));
      Preconditions.checkArgument(
          !Strings.isNullOrEmpty(getProjectId()),
          "Cannot find a project ID from either configurations or application default.");
      for (Map.Entry<String, AttributeValue> fixedAttribute : getFixedAttributes().entrySet()) {
        Preconditions.checkNotNull(fixedAttribute.getKey(), "attribute key");
        Preconditions.checkNotNull(fixedAttribute.getValue(), "attribute value");
      }
      return autoBuild();
    }
  }
}
