/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.leveldb.test;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.store.MessageStore;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.ArrayList;

/**
 */
public class ReplicationTestSupport {

    static long id_counter = 0L;
    static String payload = "";
    {
        for (int i = 0; i < 1024; i++) {
            payload += "x";
        }
    }

    static public ActiveMQTextMessage addMessage(MessageStore ms, String body) throws JMSException, IOException {
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setPersistent(true);
        message.setResponseRequired(true);
        message.setStringProperty("id", body);
        message.setText(payload);
        id_counter += 1;
        MessageId messageId = new MessageId("ID:localhost-56913-1254499826208-0:0:1:1:" + id_counter);
        messageId.setBrokerSequenceId(id_counter);
        message.setMessageId(messageId);
        ms.addMessage(new ConnectionContext(), message);
        return message;
    }

    static public ArrayList<String> getMessages(MessageStore ms) throws Exception {
        final ArrayList<String> rc = new ArrayList<String>();
        ms.recover(new MessageRecoveryListener() {
            public boolean recoverMessage(Message message) throws Exception {
                rc.add(((ActiveMQTextMessage) message).getStringProperty("id"));
                return true;
            }

            public boolean hasSpace() {
                return true;
            }

            public boolean recoverMessageReference(MessageId ref) throws Exception {
                return true;
            }

            public boolean isDuplicate(MessageId ref) {
                return false;
            }
        });
        return rc;
    }
}
