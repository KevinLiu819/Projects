const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mysql = require('mysql');

const app = express();

const con = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  database: 'shengji',
  password: 'Poroaf!23',
  port: 3306,
})

con.connect(err => {
  if (err) {
    return err;
  }
  console.log('Connected!');
});

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/', (req, res) => {
  res.send('Server is running!');
});

app.get('/results', (req, res) => {
  con.query("SELECT * FROM game", (err, results) => {
    if (err) {
      return res.send(err);
    } else {
      return res.json({
        data: results
      })
    }
  });
});

app.post('/deal', (req, res) => {
  const { id } = req.query;
  select(id, (data) => {
    const cards = deck();
    let num = 0;
    data.bottomcards = cards;
    data.player1cards = [];
    data.player2cards = [];
    data.player3cards = [];
    data.player4cards = [];
    data.player1played = [];
    data.player2played = [];
    data.player3played = [];
    data.player4played = [];
    if (data.owner === data.player1name) {
      num = 0;
    } else if (data.owner === data.player2name) {
      num = 1;
    } else if (data.owner === data.player3name) {
      num = 2;
    } else if (data.owner === data.player4name) {
      num = 3;
    }
    console.log(`Dealing cards...`);
    sendUpdate(data, id);
    timer = setInterval(() => dealCard(cards, num++, id), 250);
    res.status(200).json('{}');
  });
});

app.post('/bid', (req, res) => {
  const { id, name } = req.query;
  const suit = req.body.suit;
  const dealer = req.body.dealer;
  const num = req.body.num;
  select(id, (data) => {
    data.trumpsuit = suit;
    if (dealer) {
      data.dealer = name;
    }
    if (name === data.player1name) {
      take(data.player1cards, data.player1played);
      data.player1played = bid(data.player1cards, suit, num);
    } else if (name === data.player2name) {
      take(data.player2cards, data.player2played);
      data.player2played = bid(data.player2cards, suit, num);
    } else if (name === data.player3name) {
      take(data.player3cards, data.player3played);
      data.player3played = bid(data.player3cards, suit, num);
    } else if (name === data.player4name) {
      take(data.player4cards, data.player4played);
      data.player4played = bid(data.player4cards, suit, num);
    }
    console.log(`Successfully Bid!`);
    sendUpdate(data, id);
    res.status(200).json('{}');
  });
});

app.post('/bottom', (req, res) => {
  const { id, name } = req.query;
  select(id, (data) => {
    take(data.player1cards, data.player1played);
    take(data.player2cards, data.player2played);
    take(data.player3cards, data.player3played);
    take(data.player4cards, data.player4played);
    if (name === data.player1name) {
      take(data.player1cards, data.bottomcards);
    } else if (name === data.player2name) {
      take(data.player2cards, data.bottomcards);
    } else if (name === data.player3name) {
      take(data.player3cards, data.bottomcards);
    } else if (name === data.player4name) {
      take(data.player4cards, data.bottomcards);
    }
    console.log(`Took bottom cards.`);
    sendUpdate(data, id);
    res.status(200).json('{}');
  });
});

app.post('/discard', (req, res) => {
  const { id, name } = req.query;
  const value = req.body.value;
  select(id, (data) => {
    if (name === data.player1name) {
      data.bottomcards = discard(data.player1cards, value);
    } else if (name === data.player2name) {
      data.bottomcards = discard(data.player1cards, value);
    } else if (name === data.player3name) {
      data.bottomcards = discard(data.player1cards, value);
    } else if (name === data.player4name) {
      data.bottomcards = discard(data.player1cards, value);
    }
    console.log(`Discarded bottom cards.`);
    sendUpdate(data, id);
    res.status(200).json('{}');
  });
});

app.post('/post', (req, res) => {
  const { id } = req.query;
  const query = `SELECT state FROM game WHERE id=${id}`;
  con.query(query, (err, results) => {
    if (err) {
      return err;
    } else {
      let data = results.map(results => results.state);
      data = JSON.stringify(data);
      return res.send(data);
    }
  });
});

app.listen(5000, () => console.log(`Listening on port 5000`));

function select(id, callback) {
  const query = `SELECT state FROM game WHERE id=${id}`;
  con.query(query, (err, results) => {
    if (err) {
      return err;
    } else {
      let data = results.map(results => results.state);
      data = JSON.parse(data);
      callback(data);
    }
  });
}

function discard(hand, value) {
  const bottom = [];
  for (let i = 0; i < hand.length; i++) {
    if (value[i]) {
      value.splice(i, 1);
      bottom.concat(hand.splice(i, 1));
      i--;
    }
  }
  return bottom;
}

function take(hand, other) {
  while (other.length > 0) {
    hand.push(other.pop());
  }
  hand.sort(sort);
}

function bid(hand, suit, num) {
  for (let i = 0; i < hand.length; i++) {
    if (hand[i] === suit) {
        return hand.splice(i, num);
    }
  }
  return [];
}

function helper(cards, hand) {
  hand.push(cards.pop());
  hand.sort(sort);
}

function deck() {
  let cards = Array(108).fill(null);
  let ct = 0;
  for (let deck = 0; deck < 2; deck++) {
      for (let i = 2; i <= 14; i++) {
          cards[ct++] = i + 'S';
          cards[ct++] = i + 'H';
          cards[ct++] = i + 'D';
          cards[ct++] = i + 'C';
      }
      cards[ct++] = 'BJ';
      cards[ct++] = 'RJ';
  }
  for (let i = 0; i < cards.length; i++) {
      let rand = Math.floor(Math.random() * (cards.length - i)) + i;
      let tmp = cards[i];
      cards[i] = cards[rand];
      cards[rand] = tmp;
  }
  return cards;
}

function sort(a, b) {
  const suit = ['RJ','BJ','2S','2H','2D','2C',
                '14S','13S','12S','11S','10S','9S','8S','7S','6S','5S','4S','3S',
                '14H','13H','12H','11H','10H','9H','8H','7H','6H','5H','4H','3H',
                '14D','13D','12D','11D','10D','9D','8D','7D','6D','5D','4D','3D',
                '14C','13C','12C','11C','10C','9C','8C','7C','6C','5C','4C','3C',];
  let u, v;
  for (let i = 0; i < suit.length; i++) {
    if (a === suit[i]) {
      u = i;
    }
    if (b === suit[i]) {
      v = i;
    }
  }
  return u - v;
}

function dealCard(cards, num, id) {
  select(id, (data) => {
    const hand1 = data.player1cards;
    const hand2 = data.player2cards;
    const hand3 = data.player3cards;
    const hand4 = data.player4cards;
    if (data.bottomcards.length === 8) {
      clearInterval(timer);
      console.log('timer cleared');
      return;
    }
    if (num % 4 === 0) {
      helper(cards, hand1);
    } else if (num % 4 === 1) {
      helper(cards, hand2);
    } else if (num % 4 === 2) {
      helper(cards, hand3);
    } else if (num % 4 === 3) {
      helper(cards, hand4);
    }
    data.bottomcards = cards;
    data.player1cards = hand1;
    data.player2cards = hand2;
    data.player3cards = hand3;
    data.player4cards = hand4;
    sendUpdate(data, id);
  });
}

sendUpdate = async (res, id) => {
  const req = JSON.stringify(res);
  const query = `UPDATE game SET state='${req}' WHERE id=${id}`;
  con.query(query, (err, results) => {
    if (err) {
      console.log(err);
    } else {
      // console.log(`Success!`);
    }
  });
}
