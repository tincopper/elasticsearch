/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.rankeval;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;;

/**
 * This class represents the partial information from running the ranking evaluation metric on one
 * request alone. It contains all information necessary to render the response for this part of the
 * overall evaluation.
 */
public class EvalQueryQuality implements ToXContent, Writeable {

    /** documents seen as result for one request that were not annotated.*/
    private List<RatedDocumentKey> unknownDocs;
    private String id;
    private double qualityLevel;
    private MetricDetails optionalMetricDetails;

    public EvalQueryQuality(String id, double qualityLevel, List<RatedDocumentKey> unknownDocs) {
        this.id = id;
        this.unknownDocs = unknownDocs;
        this.qualityLevel = qualityLevel;
    }

    public EvalQueryQuality(StreamInput in) throws IOException {
        this(in.readString(), in.readDouble(), in.readList(RatedDocumentKey::new));
        this.optionalMetricDetails = in.readOptionalNamedWriteable(MetricDetails.class);
    }

    public String getId() {
        return id;
    }

    public double getQualityLevel() {
        return qualityLevel;
    }

    public List<RatedDocumentKey> getUnknownDocs() {
        return Collections.unmodifiableList(this.unknownDocs);
    }

    public void addMetricDetails(MetricDetails breakdown) {
        this.optionalMetricDetails = breakdown;
    }

    public MetricDetails getMetricDetails() {
        return this.optionalMetricDetails;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(id);
        out.writeDouble(qualityLevel);
        out.writeVInt(unknownDocs.size());
        for (RatedDocumentKey key : unknownDocs) {
            key.writeTo(out);
        }
        out.writeOptionalNamedWriteable(this.optionalMetricDetails);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(id);
        builder.field("quality_level", this.qualityLevel);
        builder.startArray("unknown_docs");
        for (RatedDocumentKey key : unknownDocs) {
            key.toXContent(builder, params);
        }
        builder.endArray();
        if (optionalMetricDetails != null) {
            builder.startObject("metric_details");
            optionalMetricDetails.toXContent(builder, params);
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EvalQueryQuality other = (EvalQueryQuality) obj;
        return Objects.equals(id, other.id) &&
                Objects.equals(qualityLevel, other.qualityLevel) &&
                Objects.equals(unknownDocs, other.unknownDocs) &&
                Objects.equals(optionalMetricDetails, other.optionalMetricDetails);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, qualityLevel, unknownDocs, optionalMetricDetails);
    }
}
