/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ozhera.log.query.core.adapter;

import org.apache.ozhera.log.query.api.enums.LogStorageTypeEnum;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for StorageAdapterFactory
 */
public class StorageAdapterFactoryTest {

    @Test
    public void testGetElasticsearchAdapter() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.ELASTICSEARCH);
        assertNotNull(adapter);
        assertEquals(LogStorageTypeEnum.ELASTICSEARCH, adapter.getStorageType());
        assertTrue(adapter instanceof ElasticsearchAdapter);
    }

    @Test
    public void testGetDorisAdapter() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.DORIS);
        assertNotNull(adapter);
        assertEquals(LogStorageTypeEnum.DORIS, adapter.getStorageType());
        assertTrue(adapter instanceof DorisAdapter);
    }

    @Test
    public void testGetMySqlAdapter() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.MYSQL);
        assertNotNull(adapter);
        assertEquals(LogStorageTypeEnum.MYSQL, adapter.getStorageType());
        assertTrue(adapter instanceof MySqlAdapter);
    }

    @Test
    public void testGetAdapterByCode() {
        StorageAdapter esAdapter = StorageAdapterFactory.getAdapter("elasticsearch");
        assertNotNull(esAdapter);
        assertEquals(LogStorageTypeEnum.ELASTICSEARCH, esAdapter.getStorageType());

        StorageAdapter dorisAdapter = StorageAdapterFactory.getAdapter("doris");
        assertNotNull(dorisAdapter);
        assertEquals(LogStorageTypeEnum.DORIS, dorisAdapter.getStorageType());

        StorageAdapter mysqlAdapter = StorageAdapterFactory.getAdapter("mysql");
        assertNotNull(mysqlAdapter);
        assertEquals(LogStorageTypeEnum.MYSQL, mysqlAdapter.getStorageType());
    }

    @Test
    public void testHasAdapter() {
        assertTrue(StorageAdapterFactory.hasAdapter(LogStorageTypeEnum.ELASTICSEARCH));
        assertTrue(StorageAdapterFactory.hasAdapter(LogStorageTypeEnum.DORIS));
        assertTrue(StorageAdapterFactory.hasAdapter(LogStorageTypeEnum.MYSQL));
    }

    @Test
    public void testGetAllAdapters() {
        var adapters = StorageAdapterFactory.getAllAdapters();
        assertNotNull(adapters);
        assertTrue(adapters.size() >= 3);
    }

    @Test
    public void testElasticsearchAdapterCapabilities() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.ELASTICSEARCH);
        assertTrue(adapter.supportsHighlight());
        assertTrue(adapter.supportsSearchAfter());
        assertTrue(adapter.supportsFullTextSearch());
        assertTrue(adapter.supportsAggregation());
    }

    @Test
    public void testDorisAdapterCapabilities() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.DORIS);
        assertFalse(adapter.supportsHighlight());
        assertFalse(adapter.supportsSearchAfter());
        assertFalse(adapter.supportsFullTextSearch());
        assertTrue(adapter.supportsAggregation());
    }

    @Test
    public void testMySqlAdapterCapabilities() {
        StorageAdapter adapter = StorageAdapterFactory.getAdapter(LogStorageTypeEnum.MYSQL);
        assertFalse(adapter.supportsHighlight());
        assertFalse(adapter.supportsSearchAfter());
        assertFalse(adapter.supportsFullTextSearch());
        assertTrue(adapter.supportsAggregation());
    }
}
