/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.etoc.weflowdaogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;



/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class WeFlowDaoGenerator {

    public static void main(String[] args) throws Exception {
    	Schema schema = new Schema(2, "com.etoc.weflow.dao");

    	addFrequentPhone(schema);
    	addFrequentQQ(schema);
    	
        new DaoGenerator().generateAll(schema, "../trunk/src-gen");
    }
    
    private static void addFrequentPhone(Schema schema) {
    	Entity note = schema.addEntity("FrequentPhone");
        note.addIdProperty().primaryKey().autoincrement();
        note.addStringProperty("phone_num").notNull().primaryKey();
    }
    
    private static void addFrequentQQ(Schema schema) {
    	Entity note = schema.addEntity("FrequentQQ");
        note.addIdProperty().primaryKey().autoincrement();
        note.addStringProperty("qq_num").notNull().primaryKey();
    }

}
