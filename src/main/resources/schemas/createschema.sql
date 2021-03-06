/* Need to switch off FK check for MySQL since there are crosswise FK references */

SET FOREIGN_KEY_CHECKS = 0;;

CREATE TABLE IF NOT EXISTS Game (
  gameID int NOT NULL UNIQUE AUTO_INCREMENT,

  name varchar(255),
  nameOfBoard varchar(100),
  phase tinyint,
  step tinyint,
  currentPlayer tinyint NULL,
  PRIMARY KEY (gameID),
  FOREIGN KEY (gameID, currentPlayer) REFERENCES Player(gameID, playerID)
);;

CREATE TABLE IF NOT EXISTS Player (
  gameID int NOT NULL,
  playerID tinyint NOT NULL,

  name varchar(255),
  colour varchar(31),
  checkpoints int,
  positionX int,
  positionY int,
  heading tinyint,

  PRIMARY KEY (gameID, playerID),
  FOREIGN KEY (gameID) REFERENCES Game(gameID)
);;

CREATE TABLE IF NOT EXISTS ProgrammingCards
(
    gameID     int NOT NULL,
    playerID   tinyint NOT NULL,
    cardName   varchar(15),
    cardNumber int,
    primary key (gameID, playerID, cardName, cardNumber),
    foreign key (gameID, playerID) references Player (gameID, playerID)
);;


CREATE TABLE IF NOT EXISTS DeckCards
(
    gameID     int NOT NULL,
    playerID   tinyint NOT NULL,
    cardName   varchar(15),
    cardNumber int,
    primary key (gameID, playerID, cardName, cardNumber),
    foreign key (gameID, playerID) references Player (gameID, playerID)
);;
SET FOREIGN_KEY_CHECKS = 1;;
