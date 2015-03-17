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
		addDownloadHistory(schema);
		addAccount(schema);    	
        new DaoGenerator().generateAll(schema, "../trunk/src-gen");
    }
    
    private static void addFrequentPhone(Schema schema) {
    	Entity note = schema.addEntity("FrequentPhone");
        note.addStringProperty("phone_num").notNull().primaryKey();
    }
    
    private static void addFrequentQQ(Schema schema) {
    	Entity note = schema.addEntity("FrequentQQ");
        note.addStringProperty("qq_num").notNull().primaryKey();
    }
    
    private static void addDownloadHistory(Schema schema) {
        Entity note = schema.addEntity("DownloadHistory");
//        note.addLongProperty("seqNo").primaryKey();
//        note.addIdProperty().primaryKey().autoincrement();
        note.addStringProperty("url").primaryKey(); //
        
        note.addIntProperty("downloadType"); //MOVIE=1,MUSIC=2,BOOK=3,APP=4
        note.addIntProperty("downloadStatus"); //WAIT=1,PERPARE=2,RUN=3,PAUSE=4,DONE=5,FAIL=6
        note.addIntProperty("downloadSize");
        note.addIntProperty("wholeSize");

        note.addStringProperty("path"); //
        note.addStringProperty("title"); //
        note.addStringProperty("detail"); //
        note.addStringProperty("picUrl"); //
        
        note.addStringProperty("mediaId"); //
        note.addStringProperty("source"); //
        note.addStringProperty("data"); //


    }

    private static void addAccount(Schema schema) {
    	Entity note = schema.addEntity("AccountInfo");
//        note.addIdProperty().primaryKey().autoincrement();
        note.addStringProperty("tel").notNull().primaryKey();
        note.addStringProperty("userid");
        note.addStringProperty("flowcoins");
        note.addStringProperty("isregistration");
        note.addStringProperty("makeflow");
        note.addStringProperty("useflow");
    }

}
