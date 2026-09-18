const SVG_NS = 'http://www.w3.org/2000/svg';

function svgElement(name, attributes = {}) {
    const element = document.createElementNS(SVG_NS, name);
    Object.entries(attributes).forEach(([key, value]) => element.setAttribute(key, String(value)));
    return element;
}

function center(element) {
    return {x: element.x + element.width / 2, y: element.y + element.height / 2};
}

function canvasPoint(canvas, event) {
    const point = canvas.createSVGPoint();
    point.x = event.clientX;
    point.y = event.clientY;
    return point.matrixTransform(canvas.getScreenCTM().inverse());
}

export function createBoardView(canvas) {
    let handlers = {select: () => {}, move: () => {}, connectTarget: () => {}};
    let rendered = {elements: new Map(), connecting: false, busy: false};
    let drag = null;

    function appendArrowDefinition() {
        const defs = svgElement('defs');
        const marker = svgElement('marker', {
            id: 'arrow', markerWidth: 10, markerHeight: 10,
            refX: 8, refY: 3, orient: 'auto', markerUnits: 'strokeWidth'
        });
        marker.append(svgElement('path', {d: 'M0,0 L0,6 L9,3 z', fill: '#52687e'}));
        defs.append(marker);
        canvas.append(defs);
    }

    function renderConnector(element, byId, selectedId) {
        const source = byId.get(element.sourceId);
        const target = byId.get(element.targetId);
        if (!source || !target) return;
        const from = center(source);
        const to = center(target);
        canvas.append(svgElement('line', {
            x1: from.x, y1: from.y, x2: to.x, y2: to.y,
            class: `connector${selectedId === element.id ? ' selected' : ''}`,
            'data-id': element.id,
            'marker-end': 'url(#arrow)'
        }));
    }

    function renderShape(element, snapshot) {
        const classes = [
            'shape',
            snapshot.selectedId === element.id ? 'selected' : '',
            snapshot.interaction.sourceId === element.id ? 'connector-source' : ''
        ].filter(Boolean).join(' ');
        const group = svgElement('g', {'data-id': element.id, class: classes});

        if (element.type === 'RECTANGLE') {
            group.append(svgElement('rect', {
                x: element.x, y: element.y, width: element.width, height: element.height,
                rx: 10, class: 'shape-body'
            }));
            const label = svgElement('text', {
                x: element.x + 14, y: element.y + element.height / 2 + 6, class: 'label'
            });
            label.textContent = element.text || 'Component';
            group.append(label);
        } else if (element.type === 'TEXT') {
            group.append(svgElement('rect', {
                x: element.x, y: element.y, width: element.width, height: element.height,
                rx: 7, class: 'text-hit-area'
            }));
            const label = svgElement('text', {
                x: element.x + 10, y: element.y + 28, class: 'label', 'font-size': 20
            });
            label.textContent = element.text || 'Text';
            group.append(label);
        }
        canvas.append(group);
    }

    function render(snapshot) {
        canvas.replaceChildren();
        appendArrowDefinition();
        const ordinaryElements = snapshot.board.elements.filter(element => element.type !== 'CONNECTOR');
        const byId = new Map(ordinaryElements.map(element => [element.id, element]));
        snapshot.board.elements
            .filter(element => element.type === 'CONNECTOR')
            .forEach(element => renderConnector(element, byId, snapshot.selectedId));
        ordinaryElements.forEach(element => renderShape(element, snapshot));

        rendered = {
            elements: new Map(snapshot.board.elements.map(element => [element.id, element])),
            connecting: snapshot.interaction.mode === 'connecting',
            busy: snapshot.remote.status === 'loading'
        };
        canvas.classList.toggle('canvas-disabled', rendered.busy);
    }

    canvas.addEventListener('pointerdown', event => {
        if (rendered.busy) return;
        const node = event.target.closest?.('[data-id]');
        if (!node) {
            handlers.select(null);
            return;
        }

        const id = node.dataset.id;
        const element = rendered.elements.get(id);
        if (rendered.connecting) {
            handlers.connectTarget(id);
            return;
        }

        handlers.select(id);
        if (!element || element.type === 'CONNECTOR') return;
        const point = canvasPoint(canvas, event);
        drag = {id, offsetX: point.x - element.x, offsetY: point.y - element.y};
        canvas.setPointerCapture(event.pointerId);
    });

    canvas.addEventListener('pointermove', event => {
        if (!drag || rendered.busy) return;
        const point = canvasPoint(canvas, event);
        handlers.move(drag.id, point.x - drag.offsetX, point.y - drag.offsetY);
    });

    canvas.addEventListener('pointerup', event => {
        if (drag && canvas.hasPointerCapture(event.pointerId)) {
            canvas.releasePointerCapture(event.pointerId);
        }
        drag = null;
    });
    canvas.addEventListener('pointercancel', () => { drag = null; });

    return Object.freeze({
        render,
        on(nextHandlers) {
            handlers = {...handlers, ...nextHandlers};
        }
    });
}
