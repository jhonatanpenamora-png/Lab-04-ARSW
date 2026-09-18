export class BoardApiError extends Error {
    constructor(status, code, message) {
        super(message);
        this.name = 'BoardApiError';
        this.status = status;
        this.code = code;
    }
}

function requiredText(value, field) {
    if (typeof value !== 'string' || !value.trim()) {
        throw new BoardApiError(0, 'CLIENT_VALIDATION', `${field} es obligatorio.`);
    }
    return value.trim();
}

async function parseResponse(response) {
    const payload = await response.json().catch(() => null);
    if (!response.ok) {
        throw new BoardApiError(
            response.status,
            payload?.code ?? 'HTTP_ERROR',
            payload?.message ?? `La petición falló con HTTP ${response.status}.`
        );
    }
    if (!payload) {
        throw new BoardApiError(response.status, 'EMPTY_RESPONSE', 'El servidor respondió sin contenido JSON.');
    }
    return payload;
}

async function request(url, options = {}) {
    try {
        return await parseResponse(await fetch(url, options));
    } catch (error) {
        if (error instanceof BoardApiError) {
            throw error;
        }
        throw new BoardApiError(0, 'NETWORK_ERROR', 'No fue posible comunicarse con el servidor.');
    }
}

export const BoardApiClient = Object.freeze({
    create(name) {
        const cleanName = requiredText(name, 'El nombre');
        return request('/api/boards', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name: cleanName})
        });
    },

    load(id) {
        const cleanId = requiredText(id, 'El Board ID');
        return request(`/api/boards/${encodeURIComponent(cleanId)}`);
    },

    save(board) {
        if (!board || !Array.isArray(board.elements)) {
            throw new BoardApiError(0, 'CLIENT_VALIDATION', 'El estado del tablero no es válido.');
        }
        const cleanId = requiredText(board.id, 'El Board ID');
        const cleanName = requiredText(board.name, 'El nombre');
        return request(`/api/boards/${encodeURIComponent(cleanId)}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name: cleanName, elements: board.elements})
        });
    }
});
