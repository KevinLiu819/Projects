import React from 'react';
import OtherHand from './OtherHand';
import Hand from './Hand';
import Bid from './Bid';
import PlayedCards from './PlayedCards';

class Game extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
        }
        this.bid = this.bid.bind(this);
        this.cardClick = this.cardClick.bind(this);
        this.value = [];
        this.bidding = false;
        this.bottom = false;
    }

    componentDidMount() {
        this.time = setInterval(() => this.callApi(), 250);
    }

    componentWillUnmount() {
        clearInterval(this.time);
    }

    callApi = async () => {
        const id = localStorage.getItem('room');
        await fetch('http://localhost:5000/post?id=' + id, {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
        })
        .then(response => response.json())
        .then(response => this.updateState(JSON.parse(response)));
    };

    updateState(res) {
        this.setState(res);
    }

    dealCards = async () => {
        console.log('deal cards');
        this.value = [];
        this.bidding = true;
        const id = localStorage.getItem('room');
        await fetch('http://localhost:5000/deal?id=' + id, {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
        });
    }

    bid = async (suit, dealer, num) => {
        const id = localStorage.getItem('room');
        const user = localStorage.getItem('username');
        await fetch('http://localhost:5000/bid?id=' + id + '&name=' + user, {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({
                suit: suit,
                dealer: dealer,
                num: num,
            })
        });
    }

    takeBottom = async () => {
        this.bidding = false;
        this.bottom = true;
        const id = localStorage.getItem('room');
        const user = localStorage.getItem('username');
        await fetch('http://localhost:5000/bottom?id=' + id + '&name=' + user, {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
        });
    }

    placeBottom = async () => {
        const tmp = this.value.slice();
        this.value = Array(25).fill(false);
        const id = localStorage.getItem('room');
        const user = localStorage.getItem('username');
        await fetch('http://localhost:5000/discard?id=' + id + '&name=' + user, {
            method: 'POST',
            headers: { 
                Accept: 'application/json',
                'Content-Type': 'application/json' 
            },
            body: JSON.stringify({
                value: tmp,
            })
        });
    }

    render() {
        return (
            <div>
                {this.state.dealer === localStorage.getItem('username')
                    ? <button onClick={() => this.dealCards()}>Start Game</button>
                    : null
                }
                {this.state.turn === localStorage.getItem('username')
                    ? <button onClick={() => this.playCards()}>Play Selected Cards</button>
                    : null
                }
                {this.canTake()
                    ? <button onClick={() => this.takeBottom()}>Take Bottom Cards</button>
                    : null
                }
                {this.canDiscard()
                    ? <button disabled={this.canPlace()} onClick={() => this.placeBottom()}>Discard Bottom Cards {this.clicked()}/8</button>
                    : null
                }
                <PlayedCards num={this.getNum()} one={this.state.player1played} two={this.state.player2played} three={this.state.player3played} four={this.state.player4played} />
                <OtherHand left={this.state.player4cards} top={this.state.player3cards} right={this.state.player2cards} />
                {this.bidding
                    ? <Bid num={this.getNum()} trumpnum={this.state.trumpnum} value={this.getCards()} change={this.bid} /> 
                    : null
                }
                <Hand value={this.getCards()} clicked={this.value} change={this.cardClick} />
            </div>
        );
    }

    getNum() {
        const name = localStorage.getItem('username');
        if (this.state.player1name === name) {
            return 1;
        } else if (this.state.player2name === name) {
            return 2;
        } else if (this.state.player3name === name) {
            return 3;
        } else if (this.state.player4name === name) {
            return 4;
        } else {
            return null;
        }
    }

    getCards() {
        const name = localStorage.getItem('username');
        if (this.state.player1name === name) {
            return this.state.player1cards;
        } else if (this.state.player2name === name) {
            return this.state.player2cards;
        } else if (this.state.player3name === name) {
            return this.state.player3cards;
        } else if (this.state.player4name === name) {
            return this.state.player4cards;
        } else {
            return null;
        }
    }

    canTake() {
        const name = localStorage.getItem('username');
        if (name !== this.state.dealer || this.bottom) {
            return false;
        }
        const len1 = this.state.player1cards.length + this.state.player1played.length;
        const len2 = this.state.player2cards.length + this.state.player2played.length;
        const len3 = this.state.player3cards.length + this.state.player3played.length;
        const len4 = this.state.player4cards.length + this.state.player4played.length;
        if (len1 === 25 && len2 === 25 && len3 === 25 && len4 === 25) {
            return true;
        } else {
            return false;
        }
    }

    canDiscard() {
        const name = localStorage.getItem('username');
        if (name !== this.state.dealer) {
            return false;
        }
        if (this.state.player1name === name && this.state.player1cards.length == 33) {
            return true;
        } else if (this.state.player2name === name && this.state.player2cards.length == 33) {
            return true;
        } else if (this.state.player3name === name && this.state.player3cards.length == 33) {
            return true;
        } else if (this.state.player4name === name && this.state.player4cards.length == 33) {
            return true;
        } else {
            return false;
        }
    }

    canPlace() {
        let ct = 0;
        for (let i = 0; i < this.value.length; i++) {
            if (this.value[i]) {
                ct++;
            }
        }
        return ct !== 8;
    }

    cardClick(i) {
        this.value[i] = !this.value[i];
    }

    clicked() {
        let ct = 0;
        for (let i = 0; i < this.value.length; i++) {
            if (this.value[i]) {
                ct++;
            }
        }
        return ct;
    }

    playCards() {
        const played = [];
        const hand = this.getCards();
        const num = this.getNum();
        for (let i = 0; i < hand.length; i++) {
            if (this.value[i]) {
                this.value.splice(i, 1);
                played.push(hand.splice(i--, 1));
            }
        }
        if (num === 1) {
            this.setState({ 
                player1cards: hand,
                player1played: played,
                turn: this.state.player2name,
            });
            const result = JSON.parse(JSON.stringify(this.state));
            result.player1cards = hand;
            result.player1played = played;
            result.turn = this.state.player2name;
            this.sendUpdate(result);
        } else if (num === 2) {
            this.setState({ 
                player2cards: hand,
                player2played: played,
                turn: this.state.player3name,
            });
            const result = JSON.parse(JSON.stringify(this.state));
            result.player2cards = hand;
            result.player2played = played;
            result.turn = this.state.player3name;
            this.sendUpdate(result);
        } else if (num === 3) {
            this.setState({ 
                player3cards: hand,
                player3played: played,
                turn: this.state.player4name,
            });
            const result = JSON.parse(JSON.stringify(this.state));
            result.player3cards = hand;
            result.player3played = played;
            result.turn = this.state.player4name;
            this.sendUpdate(result);
        } else if (num === 4) {
            this.setState({ 
                player4cards: hand,
                player4played: played,
                turn: this.state.player1name,
            });
            const result = JSON.parse(JSON.stringify(this.state));
            result.player4cards = hand;
            result.player4played = played;
            result.turn = this.state.player1name;
            this.sendUpdate(result);
        }
    }

}

export default Game;