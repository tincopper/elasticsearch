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

import org.elasticsearch.test.ESTestCase;

import java.io.IOException;


public class RatedDocumentKeyTests extends ESTestCase {

    static RatedDocumentKey createRandomRatedDocumentKey() {
        String index = randomAsciiOfLengthBetween(0, 10);
        String type = randomAsciiOfLengthBetween(0, 10);
        String docId = randomAsciiOfLengthBetween(0, 10);
        return  new RatedDocumentKey(index, type, docId);
    }

    public RatedDocumentKey createRandomTestItem() {
        return createRandomRatedDocumentKey();
    }

    public RatedDocumentKey mutateTestItem(RatedDocumentKey original) {
        String index = original.getIndex();
        String type = original.getType();
        String docId = original.getDocID();
        switch (randomIntBetween(0, 2)) {
        case 0:
            index = index + "_";
            break;
        case 1:
            type = type + "_";
            break;
        case 2:
            docId = docId + "_";
            break;
        default:
            throw new IllegalStateException("The test should only allow three parameters mutated");
        }
        return new RatedDocumentKey(index, type, docId);
    }

    public void testEqualsAndHash() throws IOException {
        RatedDocumentKey testItem = createRandomRatedDocumentKey();
        RankEvalTestHelper.testHashCodeAndEquals(testItem, mutateTestItem(testItem),
                new RatedDocumentKey(testItem.getIndex(), testItem.getType(), testItem.getDocID()));
    }
}
