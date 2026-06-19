const loginCard = document.getElementById('loginCard');
const testCard = document.getElementById('testCard');
const resumenCard = document.getElementById('resumenCard');
const loginForm = document.getElementById('loginForm');
const testForm = document.getElementById('testForm');
const loginMensaje = document.getElementById('loginMensaje');
const testMensaje = document.getElementById('testMensaje');
const itemsContenedor = document.getElementById('itemsContenedor');
const contador = document.getElementById('contador');
const resumenFinal = document.getElementById('resumenFinal');
const salirBtn = document.getElementById('salirBtn');

let aplicacionId = null;
let tiempoRestanteSegundos = 0;
let intervalo = null;
let finalizando = false;

function mostrarMensaje(elemento, texto, tipo = 'error') {
    elemento.textContent = texto || '';
    elemento.className = `mensaje ${tipo}`;
}

function cambiarVista(vista) {
    loginCard.classList.add('oculto');
    testCard.classList.add('oculto');
    resumenCard.classList.add('oculto');
    vista.classList.remove('oculto');
}

function cuerpoForm(datos) {
    return new URLSearchParams(datos).toString();
}

async function post(url, datos) {
    const respuesta = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: cuerpoForm(datos)
    });
    return respuesta.json();
}

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    mostrarMensaje(loginMensaje, 'Validando credenciales...', 'ok');

    const datos = new FormData(loginForm);
    const resultado = await post('../portal/evaluado/login', {
        usuario: datos.get('usuario'),
        contrasena: datos.get('contrasena')
    });

    if (!resultado.ok) {
        mostrarMensaje(loginMensaje, resultado.mensaje);
        return;
    }

    await cargarTest();
});

async function cargarTest() {
    const respuesta = await fetch('../portal/evaluado/test');
    const datos = await respuesta.json();

    if (!datos.ok) {
        mostrarMensaje(loginMensaje, datos.mensaje);
        cambiarVista(loginCard);
        return;
    }

    aplicacionId = datos.aplicacionId;
    document.getElementById('nombreTest').textContent = datos.test.nombre;
    document.getElementById('datosAplicacion').textContent = `Evaluado: ${datos.evaluado} | Evaluador: ${datos.evaluador}`;
    document.getElementById('instruccionesTest').textContent = datos.test.instrucciones || 'Lea cada ítem y seleccione una respuesta.';

    renderItems(datos.items);
    tiempoRestanteSegundos = Math.max(1, datos.test.tiempoLimite || 12) * 60;
    iniciarContador();
    cambiarVista(testCard);
}

function renderItems(items) {
    itemsContenedor.innerHTML = '';
    items.forEach((item) => {
        const articulo = document.createElement('article');
        articulo.className = 'item';
        articulo.innerHTML = `
            <h3>${item.numero}. ${item.enunciado}</h3>
            ${['A', 'B', 'C', 'D'].map(letra => `
                <label class="opcion">
                    <input type="radio" name="item_${item.numero}" value="${letra}" data-numero="${item.numero}">
                    <strong>${letra})</strong> ${item.opciones[letra] || ''}
                </label>
            `).join('')}
        `;
        itemsContenedor.appendChild(articulo);
    });

    itemsContenedor.querySelectorAll('input[type="radio"]').forEach((input) => {
        input.addEventListener('change', guardarRespuesta);
    });
}

async function guardarRespuesta(event) {
    const input = event.target;
    const resultado = await post('../portal/evaluado/respuesta', {
        aplicacionId,
        numeroItem: input.dataset.numero,
        respuesta: input.value
    });

    if (!resultado.ok) {
        mostrarMensaje(testMensaje, resultado.mensaje);
    } else {
        mostrarMensaje(testMensaje, 'Respuesta guardada', 'ok');
    }
}

function iniciarContador() {
    actualizarContador();
    clearInterval(intervalo);
    intervalo = setInterval(() => {
        tiempoRestanteSegundos--;
        actualizarContador();
        if (tiempoRestanteSegundos <= 0) {
            clearInterval(intervalo);
            finalizarTest(true);
        }
    }, 1000);
}

function actualizarContador() {
    const minutos = Math.floor(tiempoRestanteSegundos / 60).toString().padStart(2, '0');
    const segundos = (tiempoRestanteSegundos % 60).toString().padStart(2, '0');
    contador.textContent = `${minutos}:${segundos}`;
}

testForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    await finalizarTest(false);
});

async function finalizarTest(porTiempo) {
    if (finalizando) return;
    finalizando = true;
    clearInterval(intervalo);
    mostrarMensaje(testMensaje, porTiempo ? 'Tiempo finalizado. Cerrando test...' : 'Finalizando test...', 'ok');

    const resultado = await post('../portal/evaluado/finalizar', { aplicacionId });
    if (!resultado.ok) {
        finalizando = false;
        mostrarMensaje(testMensaje, resultado.mensaje);
        return;
    }

    resumenFinal.textContent = resultado.resumen || resultado.mensaje;
    cambiarVista(resumenCard);
}

salirBtn.addEventListener('click', async () => {
    await post('../portal/evaluado/logout', {});
    window.location.reload();
});
