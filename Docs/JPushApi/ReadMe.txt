PushMsgUtil pushmsg = new PushMsgUtil(handler, 0x88661256);
            String sendMSG = "{\"n_content\":\"\", " +
					"\"n_extras\": {\"msgtype\": \"1\", \"msgcontent\" : \"" + "你妹" + "\"," +
					"\"msghint\" : \"" + "点击查看" + "\"," + 
					"\"msgtitle\" : \"" + "有新消息" + "\"" +"}}";
            pushmsg.execute(sendMSG, "weflow");