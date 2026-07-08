// head -> tail / bottom -> top, must mirror GameBoard.java
const SNAKES = { 98: 40, 84: 63, 87: 49, 75: 15, 56: 8, 50: 5, 43: 17 };
const LADDERS = { 2: 23, 6: 45, 20: 59, 52: 72, 57: 96, 71: 92 };

// build 10x10 board in zigzag
function buildBoard() {
    const board = document.getElementById('board');
    board.innerHTML = '';
    for(let r = 0; r < 10; r++) {
        const rowFromBottom = 10 -r;
        const startNum = (rowFromBottom - 1) * 10 + 1;
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
            if(num in SNAKES) sq.classList.add('snake-square');
            if(num in LADDERS) sq.classList.add('ladder-square');
            sq.innerHTML = `<span class="sq-num">${num}</span>`;
            board.appendChild(sq);
        });
    }
    drawBoardArt(board);
}

// ── Center point of a square, in 0-100 board-percentage units ─────────────
// mirrors the zigzag layout above so art/tokens line up with the numbering
function squareCenter(num) {
    const rowFromBottom = Math.ceil(num / 10);
    const row = 10 - rowFromBottom; // 0 = top visual row
    const offset = num - (rowFromBottom - 1) * 10; // 1..10
    const col = (rowFromBottom % 2 === 0) ? (10 - offset) : (offset - 1);
    return { x: (col + 0.5) * 10, y: (row + 0.5) * 10 };
}

// ── Draw SVG artwork for every snake/ladder on top of the board ───────────
function drawBoardArt(board) {
    const svgNS = 'http://www.w3.org/2000/svg';
    const svg = document.createElementNS(svgNS, 'svg');
    svg.classList.add('board-art');
    svg.setAttribute('viewBox', '0 0 100 100');
    svg.setAttribute('preserveAspectRatio', 'none');

    Object.entries(LADDERS).forEach(([bottom, top]) => {
        drawLadder(svg, squareCenter(Number(bottom)), squareCenter(Number(top)));
    });
    Object.entries(SNAKES).forEach(([head, tail]) => {
        drawSnake(svg, squareCenter(Number(head)), squareCenter(Number(tail)));
    });

    board.appendChild(svg);
}

function drawLadder(svg, from, to) {
    const svgNS = 'http://www.w3.org/2000/svg';
    const dx = to.x - from.x, dy = to.y - from.y;
    const len = Math.hypot(dx, dy) || 1;
    const px = -(dy / len), py = dx / len; // unit vector perpendicular to the rails
    const railOffset = 2.2;

    [1, -1].forEach(side => {
        const rail = document.createElementNS(svgNS, 'line');
        rail.setAttribute('x1', from.x + px * railOffset * side);
        rail.setAttribute('y1', from.y + py * railOffset * side);
        rail.setAttribute('x2', to.x + px * railOffset * side);
        rail.setAttribute('y2', to.y + py * railOffset * side);
        rail.setAttribute('class', 'ladder-rail');
        svg.appendChild(rail);
    });

    const rungCount = Math.max(3, Math.round(len / 12));
    for (let i = 1; i < rungCount; i++) {
        const t = i / rungCount;
        const cx = from.x + dx * t, cy = from.y + dy * t;
        const rung = document.createElementNS(svgNS, 'line');
        rung.setAttribute('x1', cx + px * railOffset);
        rung.setAttribute('y1', cy + py * railOffset);
        rung.setAttribute('x2', cx - px * railOffset);
        rung.setAttribute('y2', cy - py * railOffset);
        rung.setAttribute('class', 'ladder-rung');
        svg.appendChild(rung);
    }
}

function drawSnake(svg, head, tail) {
    const svgNS = 'http://www.w3.org/2000/svg';
    const dx = tail.x - head.x, dy = tail.y - head.y;
    const len = Math.hypot(dx, dy) || 1;
    const ux = dx / len, uy = dy / len;
    const px = -uy, py = ux; // perpendicular unit vector, for the wiggle

    const segments = 6;
    const amplitude = Math.min(6, len / 6);
    const points = [];
    for (let i = 0; i <= segments; i++) {
        const t = i / segments;
        const wobble = i === 0 || i === segments ? 0 : Math.sin(t * Math.PI * 2.5) * amplitude;
        points.push({ x: head.x + dx * t + px * wobble, y: head.y + dy * t + py * wobble });
    }

    // smooth quadratic curve through the wobble points, ending exactly at the tail
    let d = `M ${points[0].x} ${points[0].y}`;
    for (let i = 1; i < points.length - 1; i++) {
        const mx = (points[i].x + points[i + 1].x) / 2;
        const my = (points[i].y + points[i + 1].y) / 2;
        d += ` Q ${points[i].x} ${points[i].y}, ${mx} ${my}`;
    }
    const last = points[points.length - 1];
    d += ` L ${last.x} ${last.y}`;

    const path = document.createElementNS(svgNS, 'path');
    path.setAttribute('d', d);
    path.setAttribute('class', 'snake-body');
    svg.appendChild(path);

    const headCircle = document.createElementNS(svgNS, 'circle');
    headCircle.setAttribute('cx', head.x);
    headCircle.setAttribute('cy', head.y);
    headCircle.setAttribute('r', 3.2);
    headCircle.setAttribute('class', 'snake-head');
    svg.appendChild(headCircle);

    [-1, 1].forEach(side => {
        const eye = document.createElementNS(svgNS, 'circle');
        eye.setAttribute('cx', head.x + px * 1.3 * side + ux);
        eye.setAttribute('cy', head.y + py * 1.3 * side + uy);
        eye.setAttribute('r', 0.6);
        eye.setAttribute('class', 'snake-eye');
        svg.appendChild(eye);
    });

    const tongue = document.createElementNS(svgNS, 'line');
    tongue.setAttribute('x1', head.x + ux * 3.2);
    tongue.setAttribute('y1', head.y + uy * 3.2);
    tongue.setAttribute('x2', head.x + ux * 5.5);
    tongue.setAttribute('y2', head.y + uy * 5.5);
    tongue.setAttribute('class', 'snake-tongue');
    svg.appendChild(tongue);
}

// ── Place P1 / P2 tokens on their current squares ─────────────────────────
function placeTokens(players) {
    document.querySelectorAll('.board-token').forEach(t => t.remove());
    const board = document.getElementById('board');

    players.forEach((player, index) => {
        if (player.position === 0) return;
        const { x, y } = squareCenter(player.position);
        const sharesSquare = players.some((other, j) => j !== index && other.position === player.position);
        const offsetX = sharesSquare ? (index === 0 ? -2.2 : 2.2) : 0;
        const token = document.createElement('div');
        token.classList.add('token', 'board-token', index === 0 ? 'p1-token' : 'p2-token');
        token.textContent = index === 0 ? 'P1' : 'P2';
        token.style.left = `${x + offsetX}%`;
        token.style.top = `${y}%`;
        board.appendChild(token);
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
// Run on page load
document.addEventListener('DOMContentLoaded', () => {
    buildBoard();
    initGame();
});
