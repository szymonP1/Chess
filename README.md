
# Chess

Simple chess game via SpringBoot API. There isn't any form of AI, game for two players.

It is advised to play via Application [Postman](https://www.postman.com/) with response body set to pretty text or raw (for better formatting)

![image](https://user-images.githubusercontent.com/83515896/172599692-5d1a3a5f-0343-4ee7-8d36-f93a510495c7.png)



## API instructions

#### To start or check status of a game

```http
  GET /api/game/start
```

To start new game with random players put -1 for all parameters

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `gameId` | `Long` | **Required**. gameId to look for (-1 to start new game) |

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `player1` | `Long` | **Required**. Id of player1 (-1 to generate guest player) |

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `player2` | `Long` | **Required**. Id of player2 (-1 to generate guest player) |

Example of response (game#{id of the game})

![image](https://user-images.githubusercontent.com/83515896/172598542-ce5948b4-7114-4cfa-9147-c0e3fc40770f.png)


#### To make move

```http
  POST /api/game/move/make
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `gameID`      | `Long` | **Required**. Id of existing game |

| Body |Example of body |
| :-------- | -------------------------------- |
| `gameID`      | ```{"initPosition": "E1","destPosition": "D2"}``` |

`initPosition` stands for coordinates of figure desired to move

`destPosition` stands for destined position

Example of response

![image](https://user-images.githubusercontent.com/83515896/172598897-10ec2aea-aa02-44a0-8e9f-e09ad710d4a3.png)

#### To make check possible moves

```http
  POST /api/game/move/check
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `gameID`      | `Long` | **Required**. Id of existing game |

| Body |Example of body |
| :-------- | -------------------------------- |
| `gameID`      | ```{"initPosition": "E1"}``` |

`initPosition` stands for coordinates of figure of which moves will be checked

Example of response:
```json
{
    "figureName": "WHITE PAWN",
    "initialPosition": "B2",
    "moves": [
        "B3"
    ]
}
```

#### To create new user

```http
  POST /api/user/create
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `name`      | `String` | **Required**. Name of player |
