/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kylin.common.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide switch between different implementations of a same interface.
 * Each implementation is identified by an integer ID.
 */
public class ImplementationSwitch<I> {

    private static final Logger logger = LoggerFactory.getLogger(ImplementationSwitch.class);

    final private Object[] instances;
    private Class<I> interfaceClz;

    public ImplementationSwitch(Map<Integer, String> impls, Class<I> interfaceClz) {
        this.interfaceClz = interfaceClz;
        this.instances = initInstances(impls);
    }

    private Object[] initInstances(Map<Integer, String> impls) {
        int maxId = 0;
        for (Integer id : impls.keySet()) {
            maxId = Math.max(maxId, id);
        }
        if (maxId > 100)
            throw new IllegalArgumentException("you have more than 100 implentations?");

        Object[] result = new Object[maxId + 1];

        for (Integer id : impls.keySet()) {
            String clzName = impls.get(id);
            try {
                result[id] = ClassUtil.newInstance(clzName);
            } catch (Exception ex) {
                logger.warn("Implementation missing " + clzName + " - " + ex);
            }
        }

        return result;
    }

    public I get(int id) {
        @SuppressWarnings("unchecked")
        I result = (I) instances[id];

        if (result == null)
            throw new IllegalArgumentException("Implementations missing, ID " + id + ", interafce " + interfaceClz.getName());

        return result;
    }
}
