import React from 'react';

function OtherHand(props) {
    
    return (
        <div>
            <div>{renderHand(props.left, 2)}</div>
            <div>{renderHand(props.top, 3)}</div>
            <div>{renderHand(props.right, 4)}</div>
        </div>
    );

    function renderHand(hand, orientation) {
        if (!hand) {
            return null;
        }
        const items = [];
        for (let i = 0; i < hand.length; i++) {
            if (orientation === 2) {
                items.push(renderLeft(hand, i));
            } else if (orientation === 3) {
                items.push(renderTop(hand, i));
            } else {
                items.push(renderRight(hand, i));
            }
        }
        return items;
    }

    function renderLeft(hand, i) {
        if (hand[i] === null) {
            return null;
        } else {
            const style = {
                position: 'absolute',
                transform: 'rotate(90deg)',
                width: '70px',
                height: '100px',
                zIndex: i,
                bottom: 15 * i + 200 + 'px',
                right: '450px',
            };
            return <img key={i} style={style} src="images/green_back.png" />
        }
    }

    function renderTop(hand, i) {
        if (hand[i] === null) {
            return null;
        } else {
            const style = {
                position: 'absolute',
                transform: 'rotate(180deg)',
                width: '70px',
                height: '100px',
                zIndex: i,
                top: '0px',
                left: 15 * i + 50 + 'px',
            };
            return <img key={i} style={style} src="images/green_back.png" />
        }
    }

    function renderRight(hand, i) {
        if (hand[i] === null) {
            return null;
        } else {
            const style = {
                position: 'absolute',
                transform: 'rotate(270deg)',
                width: '70px',
                height: '100px',
                zIndex: i,
                bottom: 15 * i + 200 + 'px',
                left: '450px',
            };
            return <img key={i} style={style} src="images/green_back.png" />
        }
    }

}

export default OtherHand;