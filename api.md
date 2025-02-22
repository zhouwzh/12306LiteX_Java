### 后端接口列表

- **注册用户**
  - 方法：`POST`
  - URL：`/api/register`
  - 描述：注册新用户。
  - 输入：username, password
  - 返回值：字符串，描述注册成功或失败
  
- **用户登录**
  
  - 方法：`POST`
  
  - URL：`/api/login`
  
  - 描述：用户登录，获取数据库自增userid。
  
  - 输入：username, password
  
  - 返回值：
  
    ```json
    [  
    	{
            "status": "success"
            "userID": ${userid}
        }
        OR
        {
            "status": "fail"
            "message": ${message}
        }
    ]
    ```
  
- **修改密码**
  
  - 方法：`POST`
  
  - URL：`/api/user/change_password`
  
  - 描述：用户修改密码
  
  - 输入：userid, oldPassword, newPassword 
  
  - 返回值：
  
    ```json
    "Reset Password Success"  or "Reset Fail, Incorrect Old Password"
    ```
  
    
  
  **修改用户信息**
  
  - 方法：`POST`
  
  - URL：`/api/user/change_information`
  
  - 描述：修改除密码外所有用户信息
  
  - 输入：
  
    ```json
    {
        "id": 1,
        "username": "alan",
        "password": "1234",
        "fname": "James",
        "lname": "Bond",
        "birthDate": "2024-12-05T10:00:00",
        "gender": "male",
        "nationality": "American",
        "email": "james.bond@example.com",
        "phone": "+1-234-567-890"
    }
    ```
  
    
  
  - 返回值：
  
    ```json
    {
        "id": 1,
        "username": "alan",
        "password": "1234",
        "fname": "James",
        "lname": "Bond",
        "birthDate": "2024-12-05T10:00:00",
        "gender": "male",
        "nationality": "American",
        "email": "james.bond@example.com",
        "phone": "+1-234-567-890"
    }
    ```
    
  
  **获得用户信息**
  
  - 方法：`POST`
  
  - URL：`/api/user/change_information`
  
  - 描述：修改除密码外所有用户信息
  
  - 输入：
  
    userid
  
    
  
  - 返回值：
  
    ```json
    {
        "id": 1,
        "username": "alan",
        "password": "1234",
        "fname": "James",
        "lname": "Bond",
        "birthDate": "2024-12-05T10:00:00",
        "gender": "male",
        "nationality": "American",
        "email": "james.bond@example.com",
        "phone": "+1-234-567-890"
    }
    ```
  
    

**查询行程**

- 方法：`POST`

- URL：`/api/user/search`

- 描述：输入出发站，到达站，日期，查询对应形成

- 输入：

  depart_station, arrival_station, datetime

  ```
  Pittsburgh, Ann Arbor, 2024-12-23
  ```

  ```
  Boston, Orlando, 2024-12-23
  ```

  

- 返回值：

  ```json
  [
      {
          "pathId": 3,
          "stations": [
              "Pittsburgh",
              "Ann Arbor"
          ],
          "arrivalTimeList": [
              "2024-12-23 10:00:00",
              "2024-12-23 13:00:00"
          ],
          "prices_A": 946.52,
          "prices_B": 451.42,
          "prices_C": 301.08,
          "departStationId": "Pittsburgh",
          "arrivalStationId": "Ann Arbor",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Blue Arrow"
      },
      {
          "pathId": 5,
          "stations": [
              "Pittsburgh",
              "Ann Arbor"
          ],
          "arrivalTimeList": [
              "2024-12-23 11:00:00",
              "2024-12-23 13:00:00"
          ],
          "prices_A": 946.52,
          "prices_B": 451.42,
          "prices_C": 301.08,
          "departStationId": "Pittsburgh",
          "arrivalStationId": "Ann Arbor",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Silver Horizon"
      },
      {
          "pathId": 13,
          "stations": [
              "Pittsburgh",
              "Ann Arbor"
          ],
          "arrivalTimeList": [
              "2024-12-23 17:00:00",
              "2024-12-23 20:00:00"
          ],
          "prices_A": 946.52,
          "prices_B": 451.42,
          "prices_C": 301.08,
          "departStationId": "Pittsburgh",
          "arrivalStationId": "Ann Arbor",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Blue Arrow"
      },
      {
          "pathId": 15,
          "stations": [
              "Pittsburgh",
              "Ann Arbor"
          ],
          "arrivalTimeList": [
              "2024-12-23 19:00:00",
              "2024-12-23 21:00:00"
          ],
          "prices_A": 946.52,
          "prices_B": 451.42,
          "prices_C": 301.08,
          "departStationId": "Pittsburgh",
          "arrivalStationId": "Ann Arbor",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Silver Horizon"
      }
  ]
  ```
  
  ```Json
  [
      {
          "pathId": 1,
          "stations": [
              "Boston",
              "New York",
              "Baltimore",
              "Richmond",
              "Orlando"
          ],
          "arrivalTimeList": [
              "2024-12-23 09:00:00",
              "2024-12-23 11:00:00",
              "2024-12-23 13:00:00",
              "2024-12-23 15:00:00",
              "2024-12-23 17:00:00"
          ],
          "prices_A": 2366.3,
          "prices_B": 1128.55,
          "prices_C": 752.6999999999999,
          "departStationId": "Boston",
          "arrivalStationId": "Orlando",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Sunrise Express"
      },
      {
          "pathId": 11,
          "stations": [
              "Boston",
              "New York",
              "Baltimore",
              "Richmond",
              "Orlando"
          ],
          "arrivalTimeList": [
              "2024-12-23 13:00:00",
              "2024-12-23 15:00:00",
              "2024-12-23 17:00:00",
              "2024-12-23 19:00:00",
              "2024-12-23 21:00:00"
          ],
          "prices_A": 2366.3,
          "prices_B": 1128.55,
          "prices_C": 752.6999999999999,
          "departStationId": "Boston",
          "arrivalStationId": "Orlando",
          "aSeatsLeft": 5,
          "bSeatsLeft": 20,
          "cSeatsLeft": 70,
          "train_name": "Sunrise Express"
      }
  ]
  ```
  

**显示行程信息**

- 方法：`GET`

- URL：`/api/user/search/trip`

- 描述：输入出发站，到达站，日期，pathid，查询对应形成

- 输入：

  pathid, depart_station, arrival_station, datetime

  ```
  13, Pittsburgh, Ann Arbor, 2024-12-23
  ```

- 输出：

  ```json
  {
      "pathId": 13,
      "stations": [
          "Pittsburgh",
          "Ann Arbor"
      ],
      "arrivalTimeList": [
          "2024-12-23 17:00:00",
          "2024-12-23 20:00:00"
      ],
      "prices_A": 0.0,
      "prices_B": 0.0,
      "prices_C": 0.0,
      "departStationId": "Pittsburgh",
      "arrivalStationId": "Ann Arbor",
      "aSeatsLeft": 5,
      "bSeatsLeft": 20,
      "cSeatsLeft": 70
  }
  ```
  
  



**订票**

- 方法：`POST`

- URL：`/api/booking

- 描述：生成ticket记录，

- 输入：

  userID, pathID, departureStationName, arrivalStationName, departureTime, arrivalTime, seatLevel, price

  Example: 

  ```json
  {
    "userId": 1,
    "pathId": 2,
    "departureStationName": "Boston",
    "arrivalStationName": "Portland",
    "departureTime": "2024-12-23 17:00:00",
    "arrivalTime": "2024-12-23 19:00:00",
    "seatLevel": "A",
    "price": 2333.5
  }
  ```

  

- 返回值：

  ```json
  {
      "Status": "Success",
      "validState": "pending",
      "invoiceId": 12,
      "paymentState": "false",
      "ticketId": 12
  }
  ```

  



**查询订单**

- 方法：`POST`

- URL：`/api/user/tickets`

- 描述：输入userid， 返回每张票的信息包括始发站，到达站，价格，日期，座位等级，和invoice 两个状态

- 输入：

  userid

  ```
  2
  ```

  

- 输出：

  ```
  [
      {
          "ticketId": 1,
          "price": 1200.0,
          "departStationName": "New York",
          "arrivalStationName": "Detroit",
          "seatLevel": "A",
          "departureTime": "2024-12-23 07:00:00",
          "arrivalTime": "2024-12-23 16:00:00",
          "trainName": "Emerald Voyage",
          "invoiceId": 1,
          "validState": "valid",
          "paymentState": "True"
      },
      {
          "ticketId": 2,
          "price": 600.0,
          "departStationName": "New York",
          "arrivalStationName": "Detroit",
          "seatLevel": "C",
          "departureTime": "2024-12-23 14:00:00",
          "arrivalTime": "2024-12-23 23:00:00",
          "trainName": "Emerald Voyage",
          "invoiceId": 2,
          "validState": "null",
          "paymentState": "False"
      }
  ]
  ```
  
  
  
  **订单支付**
  
  - 方法：`POST`
  
  - URL：`/api/user/payment`
  
  - 描述：输入ticketid，更改invoice中payment state
  
  - 输入：
  
    ticketid
  
    ```
    1
    ```
  
    ```json
    2
    ```
  
  - 输出：
  
    String  
  
    “payment success!”
  
    “payment failure: already paid"
  
    "payment failure: ticket has been cancelled"
  
    ```
    payment failure: already paid
    ```
  
    ```
    payment success!
    
    ```
    

**订单取消**

- 方法：`POST`

- URL：`/api/user/cancel`

- 描述：输入ticketid，更改invoice中payment state, 为对应path_station增加座位数

- 输入：

  ticketid

  ```
  1
  ```

  ```json
  2
  ```

- 输出：

  "cancel failure: ticket has been cancelled"

  "cancel success, refund will be returned to your account in 24 hours."

  "cancel success"

**获得所有行程**

- 方法：`GET`

- URL：`/api/path_station`

- 描述：无输入，获得所有行程

- 输入：

- 输出：

  ```json
  [
      {
          "pathid": 1,
          "start_time": "2024-12-23 07:00:00",
          "station_id": 1,
          "station_name": "Portland",
          "station_type": "start",
          "train_name": "Sunrise Express",
          "a_seats_available": 5,
          "b_seats_available": 20,
          "c_seats_available": 70
      },
      {
          "pathid": 1,
          "start_time": "2024-12-23 09:00:00",
          "station_id": 2,
          "station_name": "Boston",
          "station_type": "stop",
          "train_name": "Sunrise Express",
          "a_seats_available": 5,
          "b_seats_available": 20,
          "c_seats_available": 70
      },
      
      ....
      
       {
          "pathid": 20,
          "start_time": "2024-12-31 23:00:00",
          "station_id": 10,
          "station_name": "Detroit",
          "station_type": "end",
          "train_name": "Emerald Voyage",
          "a_seats_available": 5,
          "b_seats_available": 20,
          "c_seats_available": 70
      }
  ]   
  ```

### 锁的逻辑（轻量级锁，使用CAS）

```
import java.util.concurrent.locks.ReentrantLock;

public class TicketService {
    private final ReentrantLock lock = new ReentrantLock();

    public void bookTicket() {
        lock.lock(); // 加锁
        try {
            // 执行购票逻辑
        } finally {
            lock.unlock(); // 解锁
        }
    }
}

```



### Application.properties (OpenAI)

```
langchain4j.open-ai.chat-model.api-key=${OPENAI_KEY}
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
```



### Chat request



```json
{
    "message": "Ah, the classic quest for tickets! Miami to New York, a journey of sun to skyscrapers! Let's see if we can find you a train for that date.",
    "Instruction": "ShowTrainsFrom{}To{}at{YYYY-MM-DD}",
    "Param1": "Miami",
    "Param2": "New York",
    "Param3": "2024-12-21"
}
```

