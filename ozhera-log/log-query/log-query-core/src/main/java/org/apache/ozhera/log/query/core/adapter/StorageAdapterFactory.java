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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing storage adapters
 */
public class StorageAdapterFactory {

    private static final Map<LogStorageTypeEnum, StorageAdapter> ADAPTER_MAP = new ConcurrentHashMap<>();

    static {
        // Load adapters via ServiceLoader
        ServiceLoader<StorageAdapter> loader = ServiceLoader.load(StorageAdapter.class);
        for (StorageAdapter adapter : loader) {
            ADAPTER_MAP.put(adapter.getStorageType(), adapter);
        }
    }

    /**
     * Get adapter by storage type
     *
     * @param storageType storage type
     * @return storage adapter
     */
    public static StorageAdapter getAdapter(LogStorageTypeEnum storageType) {
        StorageAdapter adapter = ADAPTER_MAP.get(storageType);
        if (adapter == null) {
            // Default to Elasticsearch adapter
            adapter = ADAPTER_MAP.get(LogStorageTypeEnum.ELASTICSEARCH);
        }
        return adapter;
    }

    /**
     * Get adapter by storage type code
     *
     * @param storageTypeCode storage type code
     * @return storage adapter
     */
    public static StorageAdapter getAdapter(String storageTypeCode) {
        LogStorageTypeEnum storageType = LogStorageTypeEnum.fromCode(storageTypeCode);
        return getAdapter(storageType);
    }

    /**
     * Register a custom adapter
     *
     * @param adapter storage adapter
     */
    public static void registerAdapter(StorageAdapter adapter) {
        ADAPTER_MAP.put(adapter.getStorageType(), adapter);
    }

    /**
     * Get all registered adapters
     *
     * @return collection of adapters
     */
    public static Collection<StorageAdapter> getAllAdapters() {
        return Collections.unmodifiableCollection(ADAPTER_MAP.values());
    }

    /**
     * Check if adapter exists for storage type
     *
     * @param storageType storage type
     * @return true if adapter exists
     */
    public static boolean hasAdapter(LogStorageTypeEnum storageType) {
        return ADAPTER_MAP.containsKey(storageType);
    }
}
