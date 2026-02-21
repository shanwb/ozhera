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
package org.apache.ozhera.log.query.server.bootstrap;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.ozhera.log.common.Config;

/**
 * Log Query Server Bootstrap
 */
@Slf4j
public class LogQueryBootstrap {

    public static void main(String[] args) {
        try {
            log.info("Starting Log Query Server...");

            Ioc.ins().putBean(Cons.AUTO_FIND_IMPL, "true")
                    .init("com.xiaomi.mone", "com.xiaomi.youpin", "org.apache.ozhera.log.query");

            Config ins = Config.ins();
            int port = Integer.parseInt(ins.get("serverPort", "8080"));

            DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder()
                    .websocket(false)
                    .port(port)
                    .build());
            server.start();

            log.info("Log Query Server started successfully on port {}", port);
        } catch (Exception e) {
            log.error("Failed to start Log Query Server", e);
            System.exit(1);
        }
    }
}
