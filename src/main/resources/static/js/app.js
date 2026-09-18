import {BoardApiClient} from './api/board-api-client.js';
import {createBoardState} from './state/board-state.js';
import {createBoardView} from './ui/board-view.js';

const state = createBoardState();
const view = createBoardView(document.querySelector('#boardCanvas'));
const byId = id => document.getElementById(id);
const controls = {
    boardName: byId('boardName'), boardId: byId('boardId'), newBoard: byId('newBoardBtn'),
    load: byId('loadBtn'), save: byId('saveBtn'), retry: byId('retryBtn'),
    addRectangle: byId('addRectBtn'), addText: byId('addTextBtn'), connect: byId('connectBtn'),
    remove: byId('deleteBtn'), status: byId('remoteStatus'), message: byId('message')
};

let retryOperation = null;
let currentMessage = 'Crea un tablero nuevo o carga uno existente para comenzar.';

function selectedElement(snapshot) {
    return snapshot.board.elements.find(element => element.id === snapshot.selectedId) ?? null;
}

function refresh(message = currentMessage) {
    currentMessage = message;
    const snapshot = state.snapshot();
    const busy = snapshot.remote.status === 'loading';
    const hasBoard = Boolean(snapshot.board.id);
    const selected = selectedElement(snapshot);

    view.render(snapshot);
    controls.status.textContent = snapshot.remote.status[0].toUpperCase() + snapshot.remote.status.slice(1);
    controls.status.className = `status status-${snapshot.remote.status}`;
    controls.message.textContent = snapshot.remote.error?.message ?? currentMessage;
    controls.message.classList.toggle('error', snapshot.remote.status === 'error');

    if (snapshot.board.id) controls.boardId.value = snapshot.board.id;
    controls.boardName.value = snapshot.board.name;
    controls.retry.hidden = snapshot.remote.status !== 'error' || !retryOperation;

    [controls.boardName, controls.boardId, controls.newBoard, controls.load].forEach(control => {
        control.disabled = busy;
    });
    controls.save.disabled = busy || !hasBoard;
    controls.addRectangle.disabled = busy || !hasBoard;
    controls.addText.disabled = busy || !hasBoard;
    controls.remove.disabled = busy || !hasBoard || !selected;
    controls.connect.disabled = busy || !hasBoard ||
        ((!selected || selected.type === 'CONNECTOR') && snapshot.interaction.mode !== 'connecting');
    controls.connect.textContent = snapshot.interaction.mode === 'connecting'
        ? 'Cancelar conexión'
        : 'Conectar selección';
}

async function executeRemote(label, operation) {
    retryOperation = () => executeRemote(label, operation);
    state.setRemote('loading', label);
    refresh(`${label}...`);
    try {
        const board = await operation();
        state.setBoard(board);
        state.setRemote('success', label);
        retryOperation = null;
        refresh(`${label} completado.`);
    } catch (error) {
        state.setRemote('error', label, error);
        refresh();
    }
}

function canEdit() {
    const snapshot = state.snapshot();
    return Boolean(snapshot.board.id) && snapshot.remote.status !== 'loading';
}

view.on({
    select(id) {
        if (!canEdit()) return;
        state.select(id);
        refresh(id ? 'Elemento seleccionado.' : 'Selección eliminada.');
    },
    move(id, x, y) {
        if (!canEdit()) return;
        if (state.move(id, x, y)) refresh('Elemento movido localmente. Guarda para persistir.');
    },
    connectTarget(id) {
        if (!canEdit()) return;
        const connector = state.completeConnect(id);
        refresh(connector
            ? 'Conector creado localmente. Guarda para persistir.'
            : 'El destino debe ser otro elemento no conector y la conexión no debe existir.');
    }
});

controls.newBoard.addEventListener('click', () => {
    const name = controls.boardName.value;
    executeRemote('Creando tablero', () => BoardApiClient.create(name));
});

controls.load.addEventListener('click', () => {
    const id = controls.boardId.value;
    executeRemote('Cargando tablero', () => BoardApiClient.load(id));
});

controls.save.addEventListener('click', () => {
    state.setName(controls.boardName.value.trim());
    executeRemote('Guardando tablero', () => BoardApiClient.save(state.toPersistedBoard()));
});

controls.retry.addEventListener('click', () => retryOperation?.());

controls.addRectangle.addEventListener('click', () => {
    if (!canEdit()) return;
    state.addRectangle();
    refresh('Rectángulo agregado localmente. Guarda para persistir.');
});

controls.addText.addEventListener('click', () => {
    if (!canEdit()) return;
    state.addText();
    refresh('Texto agregado localmente. Guarda para persistir.');
});

controls.connect.addEventListener('click', () => {
    if (!canEdit()) return;
    const connecting = state.beginConnect();
    refresh(connecting ? 'Selecciona un segundo elemento como destino.' : 'Conexión cancelada.');
});

controls.remove.addEventListener('click', () => {
    if (!canEdit()) return;
    if (state.removeSelected()) refresh('Elemento eliminado localmente. Guarda para persistir.');
});

refresh();
