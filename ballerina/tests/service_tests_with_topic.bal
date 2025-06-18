// // Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org).
// //
// // WSO2 LLC. licenses this file to you under the Apache License,
// // Version 2.0 (the "License"); you may not use this file except
// // in compliance with the License.
// // You may obtain a copy of the License at
// //
// // http://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing,
// // software distributed under the License is distributed on an
// // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// // KIND, either express or implied.  See the License for the
// // specific language governing permissions and limitations
// // under the License.

// import ballerina/lang.runtime;
// import ballerina/test;

// byte[] payload2 = [];

// listener Listener ibmmqListener2 = new Listener({
//     channel: "DEV.APP.SVRCONN",
//     host: "localhost",
//     name: "QM1",
//     userID: "app",
//     password: "password"
// });

// @ServiceConfig {
//     config: {
//         topicName: "DEV.TOPIC.1",
//         subscriptionName: "SUB1",
//         durable: false
//     }
// }
// service Service on ibmmqListener2 {
//     remote function onMessage(Message message) returns Error? {
//         payload1 = message.payload;
//         return;
//     }
// }

// @test:Config {
//     groups: ["service", "queue"]
// }
// function testConsumeMessageFromServiceWithTopic() returns error? {
//     QueueManager queueManager = check new (
//         name = "QM1", host = "localhost", channel = "DEV.APP.SVRCONN",
//         userID = "app", password = "password"
//     );
//     Topic producer = check queueManager.accessTopic("dev", "DEV.BASE.TOPIC", OPEN_AS_SUBSCRIPTION, MQSO_CREATE);
//     check producer->put({
//         payload: "Hello World from topic".toBytes()
//     });
//     check producer->close();
//     check queueManager.disconnect();
//     runtime:sleep(2);
//     test:assertEquals(string:fromBytes(payload1), "Hello World from topic");
// }
