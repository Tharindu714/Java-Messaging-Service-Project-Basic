# Java Messaging Service (JMS) Example Project

> **Purpose**: Demonstrate a simple JMS Topic-based publish/subscribe application using GlassFish/Payara’s JMS provider. Includes publisher (`JMS-First-Client`), subscriber (`JMS-Second-Client`), server configuration screenshots, and Admin Console startup instructions.

---

## 📁 Project Structure

```
Java-Messaging-Service/
├─ JMS-First-Client/                # Publisher application (Maven project)
├─ JMS-Second-Client/               # Subscriber application (Maven project)
├─ How to excute admin console commands.txt  # Admin Console start/stop guide
├─ JMS Connection Factory.PNG       # Screenshot: JMS Connection Factory in Admin Console
├─ JMS Destination Resources.PNG    # Screenshot: JMS Topic resources
├─ JMS-First-Client Output.PNG      # Screenshot: Publisher console output
└─ JMS-Second-Client Output.PNG     # Screenshot: Subscriber console output
```

---

## 🛠 Prerequisites

* **Java 11+ JDK**
* **Apache Maven 3.6+**
* **GlassFish 7** or **Payara 6** (Java EE /Jakarta EE compatible)
* Ability to run **asadmin** CLI or use the **Admin Console**

---

## 1. Configure JMS Resources in Server

### 1.1 Start GlassFish Domain

Refer to the **Admin Console** section below for exact commands to start/stop your server.

### 1.2 Create a JMS Connection Factory

1. Open GlassFish Admin Console at `http://localhost:4848`.
2. Navigate to **Resources ▶ JMS ▶ Connection Factories ▶ New**.
3. Enter:

   * **JNDI Name**: `jms/MyConnectionFactory`
   * **Resource Type**: `javax.jms.TopicConnectionFactory`
4. Click **Save**.

![JMS Connection Factory](https://github.com/user-attachments/assets/ba100405-af1d-4efb-8084-4ac0684b0470)


Alternatively via CLI:

```bash
asadmin create-jms-resource \
  --restype javax.jms.TopicConnectionFactory \
  --property Name=MyConnectionFactory jms/MyConnectionFactory
```

### 1.3 Create a JMS Topic Destination

1. In Admin Console, go to **Resources ▶ JMS ▶ Destination Resources ▶ New**.
2. Set:

   * **JNDI Name**: `jms/MyTopic`
   * **Type**: `javax.jms.Topic`
   * **Physical Destination Name**: `MyTopic`
3. Click **Save**.

![JMS Destination Resources](https://github.com/user-attachments/assets/2c5be425-7153-45c3-b123-65dc55686a33)


Alternatively via CLI:

```bash
asadmin create-jms-resource \
  --restype javax.jms.Topic \
  --property Name=MyTopic jms/MyTopic
```

---

## 2. Publisher: JMS-First-Client

### 2.1 Build

```bash
cd JMS-First-Client
mvn clean package
```

### 2.2 Run

```bash
java -jar target/JMS-First-Client-1.0.jar
```

* Type any text and press **Enter** to publish a message to the topic.
* Type `exit` to terminate the application.

!\[Publisher Output]\(./JMS-First-Client Output.PNG)

#### Key Code Snippet

```java
InitialContext ctx = new InitialContext();
TopicConnectionFactory factory = (TopicConnectionFactory)
    ctx.lookup("jms/MyConnectionFactory");
TopicConnection conn = factory.createTopicConnection();
conn.start();
TopicSession session = conn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
Topic topic = (Topic) ctx.lookup("jms/MyTopic");
TopicPublisher publisher = session.createPublisher(topic);
Scanner scanner = new Scanner(System.in);

while (true) {
    String text = scanner.nextLine();
    if ("exit".equalsIgnoreCase(text)) break;
    TextMessage msg = session.createTextMessage(text);
    publisher.publish(msg);
}
```
![JMS-First-Client Output](https://github.com/user-attachments/assets/8d501708-52a0-4f78-b2ff-b07247b287ab)

---

## 3. Subscriber: JMS-Second-Client

### 3.1 Build

```bash
cd JMS-Second-Client
mvn clean package
```

### 3.2 Run

```bash
java -jar target/JMS-Second-Client-1.0.jar
```

* The subscriber listens indefinitely and prints messages as they arrive.

!\[Subscriber Output]\(./JMS-Second-Client Output.PNG)

#### Key Code Snippet

```java
InitialContext ctx = new InitialContext();
TopicConnectionFactory factory = (TopicConnectionFactory)
    ctx.lookup("jms/MyConnectionFactory");
TopicConnection conn = factory.createTopicConnection();
conn.start();
TopicSession session = conn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
Topic topic = (Topic) ctx.lookup("jms/MyTopic");
TopicSubscriber subscriber = session.createSubscriber(topic);

subscriber.setMessageListener(message -> {
    try {
        String text = message.getBody(String.class);
        System.out.println("Received: " + text);
    } catch (JMSException e) {
        e.printStackTrace();
    }
});

// Keep application alive
Thread.currentThread().join();
```
![JMS-Second-Client Output](https://github.com/user-attachments/assets/72e5967d-beb7-4d46-9aac-5eb6dcc26a5e)

---

## 4. Admin Console Commands

```text
# How to start Admin Console
PS> cd "C:\Program Files\glassfish7\bin"
PS> .\asadmin start-domain domain1
Waiting for domain1 to start ......
Successfully started domain1 (Admin Port: 4848)

# How to stop Admin Console
PS> .\asadmin stop-domain domain1

# Watch Real-Time Server Log
PS> Get-Content \
       "C:\Program Files\glassfish7\glassfish\domains\domain1\logs\server.log" -Wait
```

*(Adapt paths if using Payara: `C:\Program Files\payara6\bin`)*

---

## 📖 Summary & Next Steps

* You now have a fully functional JMS pub/sub example.
* Consider extending to a **Queue** model, **durable subscribers**, or **JMS Transactions**.
* Integrate with a Jakarta EE web app to push/receive messages from HTTP endpoints.

Enjoy messaging with JMS!


