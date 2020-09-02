import React from 'react';

class Bid extends React.Component {

    render() {
        if (!this.props.value) {
            return null;
        }
        return (
            <div>
                <button disabled={!this.hasJokers()} onClick={() => this.handleJoker()}>
                    {this.jokers()} No Trump
                </button>
                <button disabled={!this.hasSuit('S')} onClick={() => this.handleClick('S')}>
                    {this.numCards('S')} Spades
                </button>
                <button disabled={!this.hasSuit('D')} onClick={() => this.handleClick('D')}>
                    {this.numCards('D')} Diamonds
                </button>
                <button disabled={!this.hasSuit('H')} onClick={() => this.handleClick('H')}>
                    {this.numCards('H')} Hearts
                </button>
                <button disabled={!this.hasSuit('C')} onClick={() => this.handleClick('C')}>
                    {this.numCards('C')} Clubs
                </button>
            </div>
        );
    }

    handleClick(suit) {
        const num = this.props.trumpnum;
        this.props.change(num + suit, true, 1);
    }

    handleJoker() {
        let hand = this.props.value.slice();
        let rj = 0;
        let bj = 0;
        for (let i = 0; i < hand.length; i++) {
            if (hand[i] === 'RJ') {
                rj++;
            } else if (hand[i] === 'BJ') {
                bj++;
            }
        }
        if (rj === 2) {
            this.props.change('RJ', true, 2);
        } else if (bj === 2) {
            this.props.change('BJ', true, 2);
        }
    }

    hasJokers() {
        const hand = this.props.value.slice();
        let rj = 0;
        let bj = 0;
        for (let i = 0; i < hand.length; i++) {
            if (hand[i] === 'RJ') {
                rj++;
            } else if (hand[i] === 'BJ') {
                bj++;
            }
        }
        return rj === 2 || bj === 2;
    }

    jokers() {
        const hand = this.props.value.slice();
        let j = 0;
        for (let i = 0; i < hand.length; i++) {
            const card = hand[i];
            if (card === 'RJ' || card === 'BJ') {
                j++;
            } else if (card.substring(0, 1) === '2') {
                j++;
            }
        }
        return j;
    }

    hasSuit(suit) {
        const num = this.props.trumpnum;
        const hand = this.props.value.slice();
        const card = num + suit;
        for (let i = 0; i < hand.length; i++) {
            if (hand[i] === card) {
                return true;
            }
        }
        return false;
    }

    numCards(suit) {
        const num = this.props.trumpnum;
        const hand = this.props.value.slice();
        let ct = 0;
        for (let i = 0; i < hand.length; i++) {
            const card = hand[i];
            if (card === num + suit) {
                continue;
            }
            if (card.substring(card.length - 1, card.length) === suit) {
                ct++;
            }
        }
        return ct;
    }

}

export default Bid;