const SNAKES = [99, 70, 52, 25, 95,61, 87, 17];
const LADDERS = [4, 9, 20, 28, 40, 51, 63, 71];
// build 10x10 board in zigzag
function buildBoard() {
    const board = document.getElementById('board');
    board.innerHTML = '';
    for(let r = 0; r < 10; r++) {
        const rowFromBottom = 10 -r;
        const startNum = (rowFromBottm - 1) * 10 + 1;
        const numbers = [];

        if(rowFromBottom % 2 === 0) {
            //even rows from bottom go right to left
            for (let i = startNum + 9; i >= startNum; i--)numbers.push(i);
        }
        else {
            //odd rows from bottom go left to right
            for (let i = startNum; i <=startNum + 9; i++)numbers.push(i);
        }
        numbers.forEach(num => {
            const sq = document.createElement('div');
            sq.classList.add('square');
            sq.id = `square-${num}`;
            if(SNAKES.includes(num)) sq.classList.add('snake-square');
            if(LADDERS.includes(num)) sq.classList.add('ladder-square');
            sq.innerHTML = `<span class="sq-num">${num}</span>`;
            board.appendChild(sq);
        });
    }
}
// ── Place P1 / P2 tokens on their current squares ─────────────────────────
function placeTokens(players) {
    document.querySelectorAll('.token').forEach(t => t.remove());

    players.forEach((player, index) => {
        if (player.position === 0) return;
        const sq = document.getElementById(`square-${player.position}`);
        if (!sq) return;
        const token = document.createElement('div');
        token.classList.add('token', index === 0 ? 'p1-token' : 'p2-token');
        token.textContent = index === 0 ? 'P1' : 'P2';
        sq.appendChild(token);
    });
}

// ── Refresh all UI elements from a GameState object ───────────────────────
function updateUI(state) {
    // Dice
    document.getElementById('dice-display').textContent = state.diceValue || '?';

    // Message
    document.getElementById('message').textContent = state.message;

    // Player position labels
    document.getElementById('p1-position').textContent = `Square: ${state.players[0].position}`;
    document.getElementById('p2-position').textContent = `Square: ${state.players[1].position}`;

    // Active player highlight
    document.getElementById('player1-card').classList.toggle('active', state.currentPlayerIndex === 0);
    document.getElementById('player2-card').classList.toggle('active', state.currentPlayerIndex === 1);

    // Move tokens on board
    placeTokens(state.players);

    // Win condition
    if (state.status === 'FINISHED') {
        document.getElementById('winner-text').textContent = `🎉 ${state.winner} wins!`;
        document.getElementById('winner-banner').style.display = 'block';
        document.getElementById('roll-btn').disabled = true;
    } else {
        document.getElementById('winner-banner').style.display = 'none';
        document.getElementById('roll-btn').disabled = false;
    }
}

// ── API calls ──────────────────────────────────────────────────────────────
function initGame() {
    fetch('/api/game/init')
        .then(res => res.json())
        .then(state => updateUI(state))
        .catch(err => console.error('Init failed:', err));
}

function rollDice() {
    fetch('/api/game/roll', { method: 'POST' })
        .then(res => res.json())
        .then(state => updateUI(state))
        .catch(err => console.error('Roll failed:', err));
}

function resetGame() {
    fetch('/api/game/reset', { method: 'POST' })
        .then(res => res.json())
        .then(state => updateUI(state))
        .catch(err => console.error('Reset failed:', err));
}