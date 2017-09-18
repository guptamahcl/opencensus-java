/*
 * Copyright 2017, OpenCensus Authors
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

package io.opencensus.implcore.stats;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.Iterables;
import io.opencensus.common.Function;
import io.opencensus.common.Functions;
import io.opencensus.stats.Aggregation;
import io.opencensus.stats.AggregationData;
import io.opencensus.stats.AggregationData.CountData;
import io.opencensus.stats.AggregationData.MeanData;
import io.opencensus.stats.AggregationData.SumData;
import io.opencensus.tags.TagValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Stats test utilities. */
final class StatsTestUtil {

  private StatsTestUtil() {}

  /**
   * Creates an {@link AggregationData} by adding the given sequence of values, based on the
   * definition of the given {@link Aggregation}.
   *
   * @param aggregation the {@code Aggregation} to apply the values to.
   * @param values the values to add to the {@code MutableAggregation}s.
   * @return an {@code AggregationData}.
   */
  static AggregationData createAggregationData(
      Aggregation aggregation, double... values) {
    MutableAggregation mutableAggregation = MutableViewData.createMutableAggregation(aggregation);
    for (double value : values) {
      mutableAggregation.add(value);
    }
    return MutableViewData.createAggregationData(mutableAggregation);
  }

  /**
   * Compare the actual and expected AggregationMap within the given tolerance.
   *
   * @param expected the expected map.
   * @param actual the actual mapping from {@code List<TagValue>} to
   *     {@code AggregationData}.
   * @param tolerance the tolerance used for {@code double} comparison.
   */
  static void assertAggregationMapEquals(
      Map<? extends List<? extends TagValue>, ? extends AggregationData> actual,
      Map<? extends List<? extends TagValue>, ? extends AggregationData> expected,
      double tolerance) {
    assertThat(actual.keySet()).containsExactlyElementsIn(expected.keySet());
    for (List<? extends TagValue> tagValues : actual.keySet()) {
      assertAggregationDataEquals(expected.get(tagValues), actual.get(tagValues), tolerance);
    }
  }

  /**
   * Compare the expected and actual {@code AggregationData} within the given tolerance.
   *
   * @param expected the expected {@code AggregationData}.
   * @param actual the actual {@code AggregationData}.
   * @param tolerance the tolerance used for {@code double} comparison.
   */
  static void assertAggregationDataEquals(
      AggregationData expected, final AggregationData actual, final double tolerance) {

    expected.match(
        new Function<SumData, Void>() {
          @Override
          public Void apply(SumData arg) {
            assertThat(actual).isInstanceOf(SumData.class);
            assertThat(((SumData) actual).getSum()).isWithin(tolerance).of(arg.getSum());
            return null;
          }
        },
        new Function<CountData, Void>() {
          @Override
          public Void apply(CountData arg) {
            assertThat(actual).isInstanceOf(CountData.class);
            assertThat(((CountData) actual).getCount()).isEqualTo(arg.getCount());
            return null;
          }
        },
        Functions.<Void>throwIllegalArgumentException(),
        Functions.<Void>throwIllegalArgumentException(),
        new Function<MeanData, Void>() {
          @Override
          public Void apply(MeanData arg) {
            assertThat(actual).isInstanceOf(MeanData.class);
            assertThat(((MeanData) actual).getMean()).isWithin(tolerance).of(arg.getMean());
            return null;
          }
        },
        Functions.<Void>throwIllegalArgumentException(),
        Functions.<Void>throwIllegalArgumentException());
  }

  private static List<Long> removeTrailingZeros(List<Long> longs) {
    if (longs == null) {
      return null;
    }
    List<Long> truncated = new ArrayList<Long>(longs);
    while (!truncated.isEmpty() && Iterables.getLast(truncated) == 0) {
      truncated.remove(truncated.size() - 1);
    }
    return truncated;
  }
}
