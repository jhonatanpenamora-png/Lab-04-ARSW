function uid(prefix) {
    const id = globalThis.crypto?.randomUUID?.() ?? `${Date.now()}-${Math.random().toString(16).slice(2)}`;
    return `${prefix}-${id}`;
}

function clone(value) {
    return structuredClone(value);
}

function errorData(error) {
    if (!error) return null;
    return {
        status: Number.isFinite(error.status) ? error.status : 0,
        code: error.code ?? 'UNEXPECTED_ERROR',
        message: error.message ?? 'Ocurrió un error inesperado.'
    };
}

export function createBoardState() {
    let board = {id: null, name: 'Architecture Board', elements: []};
    let selectedId = null;
    let interaction = {mode: 'idle', sourceId: null};
    let remote = {status: 'idle', operation: null, error: null};

    function elementById(id) {
        return board.elements.find(element => element.id === id) ?? null;
    }

    function nonConnector(id) {
        const element = elementById(id);
        return element && element.type !== 'CONNECTOR' ? element : null;
    }

    function addElement(element) {
        board = {...board, elements: [...board.elements, element]};
        selectedId = element.id;
        interaction = {mode: 'idle', sourceId: null};
        return clone(element);
    }

    return Object.freeze({
        snapshot() {
            return clone({board, selectedId, interaction, remote});
        },

        setBoard(next) {
            if (!next || typeof next.id !== 'string' || !Array.isArray(next.elements)) {
                throw new Error('El servidor entregó un tablero inválido.');
            }
            board = clone(next);
            selectedId = null;
            interaction = {mode: 'idle', sourceId: null};
        },

        setName(name) {
            board = {...board, name};
        },

        setRemote(status, operation = null, error = null) {
            if (!['idle', 'loading', 'success', 'error'].includes(status)) {
                throw new Error(`Estado remoto desconocido: ${status}`);
            }
            remote = {status, operation, error: errorData(error)};
        },

        select(id) {
            selectedId = id && elementById(id) ? id : null;
        },

        addRectangle() {
            const offset = board.elements.filter(e => e.type !== 'CONNECTOR').length * 18;
            return addElement({
                id: uid('rect'), type: 'RECTANGLE', x: 90 + offset, y: 80 + offset,
                width: 190, height: 80, text: 'Component', sourceId: null, targetId: null
            });
        },

        addText() {
            const offset = board.elements.filter(e => e.type !== 'CONNECTOR').length * 18;
            return addElement({
                id: uid('text'), type: 'TEXT', x: 130 + offset, y: 220 + offset,
                width: 170, height: 42, text: 'Text', sourceId: null, targetId: null
            });
        },

        move(id, x, y) {
            const element = nonConnector(id);
            if (!element || !Number.isFinite(x) || !Number.isFinite(y)) return false;
            const nextX = Math.max(0, Math.min(1000 - element.width, x));
            const nextY = Math.max(0, Math.min(600 - element.height, y));
            board = {
                ...board,
                elements: board.elements.map(current =>
                    current.id === id ? {...current, x: nextX, y: nextY} : current
                )
            };
            selectedId = id;
            return true;
        },

        beginConnect() {
            if (interaction.mode === 'connecting') {
                interaction = {mode: 'idle', sourceId: null};
                return false;
            }
            if (!nonConnector(selectedId)) return false;
            interaction = {mode: 'connecting', sourceId: selectedId};
            return true;
        },

        completeConnect(targetId) {
            const source = nonConnector(interaction.sourceId);
            const target = nonConnector(targetId);
            if (interaction.mode !== 'connecting' || !source || !target || source.id === target.id) {
                return null;
            }
            const duplicated = board.elements.some(element =>
                element.type === 'CONNECTOR' &&
                ((element.sourceId === source.id && element.targetId === target.id) ||
                 (element.sourceId === target.id && element.targetId === source.id))
            );
            if (duplicated) return null;
            return addElement({
                id: uid('connector'), type: 'CONNECTOR', x: 0, y: 0,
                width: 0, height: 0, text: '', sourceId: source.id, targetId: target.id
            });
        },

        removeSelected() {
            if (!selectedId) return false;
            const removedId = selectedId;
            const originalSize = board.elements.length;
            board = {
                ...board,
                elements: board.elements.filter(element =>
                    element.id !== removedId &&
                    element.sourceId !== removedId &&
                    element.targetId !== removedId
                )
            };
            selectedId = null;
            interaction = {mode: 'idle', sourceId: null};
            return board.elements.length !== originalSize;
        },

        toPersistedBoard() {
            return clone(board);
        }
    });
}
