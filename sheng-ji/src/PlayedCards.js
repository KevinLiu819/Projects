import React from 'react';

function PlayedCards(props) {

    return (
        <div>
            <div>{renderHand(props.one, 5 - props.num)}</div>
            <div>{renderHand(props.two, 6 - props.num)}</div>
            <div>{renderHand(props.three, 7 - props.num)}</div>
            <div>{renderHand(props.four, 8 - props.num)}</div>
        </div>
    );

    function renderHand(cards, num) {
        if (!cards) {
            return null;
        }
        const items = [];
        for (let i = 0; i < cards.length; i++) {
            if(num % 4 === 0) {
                items.push(renderBottom(cards[i], i));
            } else if (num % 4 === 1) {
                items.push(renderRight(cards[i], i));
            } else if (num % 4 === 2) {
                items.push(renderTop(cards[i], i));
            } else if (num % 4 === 3) {
                items.push(renderLeft(cards[i], i));
            }
        }
        return items;
    }

    function renderBottom(card, i) {
        const image = 'images/' + card + '.png';
        const style = {
            position: 'absolute',
            width: '70px',
            height: '100px',
            zIndex: i,
            top: '550px',
            left: 15 * i + 225 + 'px',
        };
        return <img key={i} style={style} src={image} />
    }

    function renderRight(card, i) {
        const image = 'images/' + card + '.png';
        const style = {
            position: 'absolute',
            width: '70px',
            height: '100px',
            zIndex: i,
            top: '300px',
            left: 15 * i + 300 + 'px',
        };
        return <img key={i} style={style} src={image} />
    }

    function renderTop(card, i) {
        const image = 'images/' + card + '.png';
        const style = {
            position: 'absolute',
            width: '70px',
            height: '100px',
            zIndex: i,
            top: '150px',
            left: 15 * i + 225 + 'px',
        };
        return <img key={i} style={style} src={image} />
    }

    function renderLeft(card, i) {
        const image = 'images/' + card + '.png';
        const style = {
            position: 'absolute',
            width: '70px',
            height: '100px',
            zIndex: i,
            top: '300px',
            left: 15 * i + 150 + 'px',
        };
        return <img key={i} style={style} src={image} />
    }

}

export default PlayedCards;